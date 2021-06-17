package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CorsoDAO;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/ElencoEsami")
public class ElencoEsami extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public ElencoEsami() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// recupero nomeCorso dai parametri della richiesta
		String nomeCorso;
		try {
			nomeCorso = request.getParameter("nomeCorso");
			if(nomeCorso == null) 
				throw new Exception();
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Richiesta incompleta. Parametri mancanti.");
			return;
		}

		/**
		 * La homepage mostra, sia per Studente sia per Professore, gli stessi contenuti
		 * cio� una lista di corsi e per ogni corso una lista di esami.
		 */
		
		// recupero i diversi corsi con il nome specificato
		List<Corso> corsi;
		try {
			corsi = this.getCorsoContentByUserRole(user, nomeCorso);
		} catch (SQLException e) {
			//TODO: modificare questo possibilmente
			if(e.getSQLState() != "S1000") {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			} else {
				//TODO controllo contro web parameters tampering - accesso ad un corso a cui lo studente non è iscritto
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Il corso ricercato non esiste o non sei iscritto a questo corso.");
			}
			return;
		}

		// recupero gli esami dal corso specificato
		List<List<Esame>> corsiEsami = new ArrayList<List<Esame>>();
		try {
			corsiEsami = this.getEsamiFromCorsoByUserRole(corsi,user);
			
			// controllo contro web parameters tampering - accesso a corsi di un altro docente
			if(corsiEsami.isEmpty()) {
				throw new Exception("Il corso ricercato non esiste o non sei il docente di questo corso.");
			}
		} catch (SQLException e) {
			// se l'eccezione non è data da un set vuoto di risultati viene loggata e viene
			// mostrata una pagina di errore, altrimenti viene gestita internamente
			if(e.getSQLState() != "S1000") {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
				return;
			} else {
				for(Corso corso: corsi) {
					corsiEsami.add(new ArrayList<Esame>());
				}
			}
		} catch (Exception e) {
			//TODO 
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/ElencoEsami.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("allCorsi", corsi);
		ctx.setVariable("allCorsiEsami", corsiEsami);
		templateEngine.process(path, ctx, response.getWriter());
	}

	private List<List<Esame>> getEsamiFromCorsoByUserRole(List<Corso> corsi, User user) throws SQLException{
		List<List<Esame>> corsiEsami = new ArrayList<List<Esame>>();
		EsameDAO esameDao = new EsameDAO(connection);
		if(user.getRuolo().equals("teacher")) {
			for (Corso c : corsi) {
				corsiEsami.add(esameDao.getEsamiFromCorso(c.getId()));
			}
		}else if(user.getRuolo().equals("student")) {
			for (Corso c : corsi) {
				corsiEsami.add(esameDao.getEsamiFromStudenteCorso(user.getMatricola(),c.getId()));
			}
		}
		return corsiEsami;
	}

	private List<Corso> getCorsoContentByUserRole(User user, String nome) throws SQLException{
		List<Corso> corsi = null;
		// nelle righe seguenti viene fatta un'interrogazione al db che pu�
		// lanciare una SQLException, la gestione dell'eccezione viene fatta
		// dal chiamante di questo metodo
		CorsoDAO corsoDao = new CorsoDAO(connection);
		if(user.getRuolo().equals("teacher"))
			corsi = corsoDao.getCorsiFromMatricolaProfessore(user.getMatricola(), nome);
		else if(user.getRuolo().equals("student"))
			corsi = corsoDao.getCorsiFromMatricolaStudente(user.getMatricola(), nome);
		return corsi;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

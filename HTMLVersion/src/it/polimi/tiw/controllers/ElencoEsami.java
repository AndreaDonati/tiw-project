package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CorsoDAO;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.utils.MyHttpServlet;

@WebServlet("/ElencoEsami")
public class ElencoEsami extends MyHttpServlet {
	private static final long serialVersionUID = 1L;

	public ElencoEsami() {
		super();
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
			redirectToErrorPage(request,response, "Richiesta incompleta. Parametri mancanti.");
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
			if(e.getSQLState() != "S1000") {
				redirectToErrorPage(request,response, e.toString());
			} else {
				// controllo contro web parameters tampering - accesso ad un corso a cui lo studente non è iscritto
				redirectToErrorPage(request,response,"Il corso ricercato non esiste o non sei iscritto a questo corso.");
			}
			return;
		}

		// recupero gli esami dal corso specificato
		List<List<Esame>> corsiEsami = new ArrayList<List<Esame>>();
		try {
			corsiEsami = this.getEsamiFromCorsoByUserRole(corsi,user);
			
			 
			if(corsiEsami.isEmpty()) {
				throw new Exception("Il corso ricercato non esiste o non sei il docente di questo corso.");
			}
		} catch (SQLException e) {
			// se l'eccezione non è data da un set vuoto di risultati viene loggata e viene
			// mostrata una pagina di errore, altrimenti viene gestita internamente
			if(e.getSQLState() != "S1000") {
				redirectToErrorPage(request,response, e.toString());
				return;
			} else {
				for(Corso corso: corsi) {
					corsiEsami.add(new ArrayList<Esame>());
				}
			}
		} catch (Exception e) {
			redirectToErrorPage(request,response,e.toString().replace("java.lang.Exception: ",""));
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
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

}

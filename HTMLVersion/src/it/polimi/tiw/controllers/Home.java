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

@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public Home() {
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
		
//		PROVA + temporaneo
		session.setAttribute("mail", user.getMail());
		session.setAttribute("lastAccessedTime", new java.util.Date());
//		---------------
		
		/**
		 * La homepage mostra, sia per Studente sia per Professore, gli stessi contenuti
		 * cioè una lista di corsi e per ogni corso una lista di esami.
		 */
		
		// recupero i corsi associati allo user
		// se lo user è Studente: i corsi che frequenta
		// se lo user è Profesore: i corsi che insegna
		List<Corso> corsi;
		try {
			corsi = this.getCorsiContentByUserRole(user);
		} catch (SQLException e) {
			//e1.printStackTrace();
			//TODO: modificare questo possibilmente
			// l'eccezione indica un errore nella query al db
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover all users "+e.toString());
			return;
		}
		
		// recupero gli esami dai ai corsi associati allo user
		List<List<Esame>> corsiEsami;
		try {
			corsiEsami = this.getEsamiFromCorsi(corsi);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover all users "+e.toString());
			return;
		}
				
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/Login/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("allCorsi", corsi);
		ctx.setVariable("allCorsiEsami", corsiEsami);
		templateEngine.process(path, ctx, response.getWriter());
	}

	private List<List<Esame>> getEsamiFromCorsi(List<Corso> corsi) throws SQLException{
		List<List<Esame>> corsiEsami = new ArrayList<List<Esame>>();
		// QUESTA E' UNA PROVA KEKW
		EsameDAO esameDao = new EsameDAO(connection);
		for (Corso c : corsi) {
			corsiEsami.add(esameDao.getEsamiFromCorso(c.getId()));
		}

		return corsiEsami;
	}

	private List<Corso> getCorsiContentByUserRole(User user) throws SQLException{
		List<Corso> corsi = null;
		// nelle righe seguenti viene fatta un'interrogazione al db che può
		// lanciare una SQLException, la gestione dell'eccezione viene fatta
		// dal chiamante di questo metodo
		//TODO: decidere se implementarlo così opppure differenziare nel DAO
		CorsoDAO corsoDao = new CorsoDAO(connection);
		if(user.getRuolo().equals("teacher"))
			corsi = corsoDao.getCorsiFromMatricolaProfessore(user.getMatricola());
		else if(user.getRuolo().equals("student"))
			corsi = corsoDao.getCorsiFromMatricolaStudente(user.getMatricola());
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

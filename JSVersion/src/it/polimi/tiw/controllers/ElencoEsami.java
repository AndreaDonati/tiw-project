package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CorsoDAO;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/ElencoEsami")
public class ElencoEsami extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public ElencoEsami() {
		super();
	}

	public void init() throws ServletException {

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
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore");
			return;
		}

		// recupero gli esami dal corso specificato
		List<List<Esame>> corsiEsami = null;
		try {
			corsiEsami = this.getEsamiFromCorsoByUserRole(corsi,user);
		} catch (SQLException e) {
			// se l'eccezione non è data da un set vuoto di risultati viene loggata e viene
			// mostrata una pagina di errore, altrimenti viene gestita internamente
			if(e.getSQLState() != "S1000") {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore");
				return;
			}
		}
				
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		Gson gson = new Gson();
		
//		Map<Corso,List<Esame>> map = new HashMap<Corso,List<Esame>>();
//		for(int i = 0; i<corsi.size(); i++) {
//			map.put(corsi.get(i), corsiEsami.get(i));
//		}
		
		String jsonObj = gson.toJson(corsi);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		//response.getWriter().write(jsonObj);
		String jsonObj2 = gson.toJson(corsiEsami);
		response.getWriter().write("["+jsonObj+","+jsonObj2+"]");

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

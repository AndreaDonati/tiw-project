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
import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.MyHttpServlet;

/**
 * Servlet implementation class GetResults
 */
@WebServlet("/getResults")
public class GetResults extends MyHttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetResults() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//recupero lo user dalla sessione
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// recupero i parametri dalla richiesta
		String idEsameParam;
		int idEsame;
		try {
			idEsameParam = request.getParameter("idEsame");
			if(idEsameParam == null) 
				throw new Exception();
			idEsame = Integer.parseInt(idEsameParam);
		} catch (Exception e) {
			redirectToErrorPage(request,response, "Richiesta incompleta. Parametri mancanti.");
			return;
		}


		// recupero i parametri dalla richiesta
		String ordine;
		String campo;
		ordine = request.getParameter("ordine");
		campo = request.getParameter("campo");

		// se ordine o campo non sono presenti metto quelli di default
		// se la colonna "old" e quella "new" sono diverse, devo resettare l'ordine e mettere quello di default
		// i dati arrivano dal db ordinati per matricola in modo ASC  
		if(campo == null)
			campo = "matricola";
		if(ordine == null)
			ordine = "ASC";
		


		// recupero i risultati dell'esame chiesto dallo user:
		// user == teacher: recupero tutti i risultati dell'esame
		// user == student: recupero il solo risultato dello studente
		List<Esaminazione> risultati;
		try {
			risultati = this.getRisultatiEsamiByUserRole(user, idEsame, ordine, campo);
		} catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
			return;
		} catch (Exception e) {
			// controllo contro web parameters tampering - accesso ad esami di un altro utente
			redirectToErrorPage(request,response,e.toString().replace("java.lang.Exception: ",""));
			return;
		}
		
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/RisultatiEsame.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		// setto queste variabili solo se ci sono risultati per l'esame
		if(risultati.get(0).getId() != -1) {
			ctx.setVariable("arePubblicabili",arePubblicabili(risultati));
			ctx.setVariable("areVerbalizzabili",areVerbalizzabili(risultati));
		}
		ctx.setVariable("risultati", risultati);
		ctx.setVariable("campo", campo);
		ctx.setVariable("ordine", ordine.equals("ASC") ? "DESC" : "ASC");
		templateEngine.process(path, ctx, response.getWriter());		
	}

	private List<Esaminazione> getRisultatiEsamiByUserRole(User user, int idEsame, String ordine, String campo) throws Exception{
		List<Esaminazione> risultati = null;
		
		EsameDAO esameDao = new EsameDAO(connection);
		UserDAO userDAO = new UserDAO(connection);
		if(user.getRuolo().equals("teacher")) {
			if(userDAO.controllaDocente(idEsame, user.getMatricola()))
				risultati = esameDao.getRisultatiEsameProfessore(idEsame, ordine, campo, user.getMatricola());
			else 
				throw new Exception("L'esame ricercato non esiste o non sei il docente di questo esame.");
		}else if(user.getRuolo().equals("student"))
			if(userDAO.controllaStudente(idEsame, user.getMatricola()))
				risultati = esameDao.getRisultatiEsameStudente(user.getMatricola(), idEsame);
			else
				throw new Exception("L'esame ricercato non esiste o non sei iscritto a questo esame.");
		return risultati;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private boolean areVerbalizzabili(List<Esaminazione> risultati) {
		if(risultati == null)
			return false;
		
		for (Esaminazione esaminazione : risultati) {
			if(esaminazione.isVerbalizzabile())
				return true;
		}
		return false;
	}

	private boolean arePubblicabili(List<Esaminazione> risultati) {
		if(risultati == null)
			return false;
		
		for (Esaminazione esaminazione : risultati) {
			if(esaminazione.isPubblicabile())
				return true;
		}
		return false;
	}
}

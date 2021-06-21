package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Verbale;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.dao.VerbaleDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.MyHttpServlet;

@WebServlet("/verbalizzaVoti")
public class VerbalizzaVoti extends MyHttpServlet {
	private static final long serialVersionUID = 1L;
	
    public VerbalizzaVoti() {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int idEsame;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
		} catch (Exception e) {
			redirectToErrorPage(request,response,"Identificativo dell'esame errato.");
			return;
		}
		
		//controllo che l'utente sia il docente relativo al corso dell'esame
		UserDAO userDAO = new UserDAO(connection);
		try {
			if(!userDAO.controllaDocente(idEsame, user.getMatricola()))
				throw new Exception("L'esame ricercato non esiste o non sei il docente di questo esame.");
		}  catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
			return;
		} catch (Exception e) {
			// controllo contro web parameters tampering - pubblicazione voti di un esame di un altro docente
			redirectToErrorPage(request,response,e.toString().replace("java.lang.Exception: ",""));
			return;
		}
		
		
		// controllo se ci sono voti con stato "pubblicato"
		// se non ci sono allora non ci sono voti da verbalizzare e reindirizzo
		// il professore alla stessa pagina
		EsameDAO esameDao = new EsameDAO(connection);
		List<Esaminazione> risultati = null;
		try {
			risultati = esameDao.getRisultatiEsameProfessore(idEsame);
		} catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
			return;
		}
		if(!checkVerbalizzabili(risultati)) {
			String path = getServletContext().getContextPath() + "/getResults?idEsame="+idEsame;
			response.sendRedirect(path);
			return;
		}
		
		// cambio lo stato dei voti relativi all'esame specificato in 'verbalizzato'
		// e creo un nuovo verbale relativo all'esame
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		int idVerbale = 0;
		try {
			idVerbale = esaminazioneDAO.recordGrades(idEsame);
		} catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
			return;
		}
		
		// recupero il verbale appena creato dal db
		VerbaleDAO verbaleDao = new VerbaleDAO(connection);
		Verbale verbale = null;
		try {
			verbale = verbaleDao.getVerbale(idVerbale);
		} catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
			return;
		}

		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/Prof/Verbale.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("verbale", verbale);
		templateEngine.process(path, ctx, response.getWriter());
	}

	private boolean checkVerbalizzabili(List<Esaminazione> risultati) {
		if(risultati == null)
			return false;
		
		for (Esaminazione esaminazione : risultati) 
			if(esaminazione.getStato().equals("pubblicato"))
				return true;
		
		return false;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

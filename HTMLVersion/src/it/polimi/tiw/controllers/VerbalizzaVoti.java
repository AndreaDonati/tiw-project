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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.Verbale;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.dao.VerbaleDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/verbalizzaVoti")
public class VerbalizzaVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
    public VerbalizzaVoti() {
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idEsame;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Identificativo dell'esame errato");
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
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.toString());
			return;
		}
		if(!checkVerbalizzabili(risultati)) {
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Non ci sono voti da verbalizzare");
			// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
			// seleziono il path corretto in base al ruolo dello user
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
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// recupero il verbale appena creato dal db
		VerbaleDAO verbaleDao = new VerbaleDAO(connection);
		Verbale verbale = null;
		try {
			verbale = verbaleDao.getVerbale(idVerbale);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
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

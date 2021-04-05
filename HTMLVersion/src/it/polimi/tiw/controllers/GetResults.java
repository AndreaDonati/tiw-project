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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GetResults
 */
@WebServlet("/getResults")
public class GetResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetResults() {
        super();
        // TODO Auto-generated constructor stub
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
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Richiesta incompleta. Parametri mancanti.");
			return;
		}
		
		// recupero la l'etichetta della colonna dalla sessione
		String oldColonna = session.getAttribute("colonna");

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
		if(ordine == null || !oldColonna.equals(campo))
			ordine = "ASC";

		// aggiorno la colonna selezionata
		session.setAttribute("colonna", campo);


		// recupero i risultati dell'esame chiesto dallo user:
		// user == teacher: recupero tutti i risultati dell'esame
		// user == student: recupero il solo risultato dello studente
		List<Esaminazione> risultati;
		try {
			risultati = this.getRisultatiEsamiByUserRole(user, idEsame, ordine, campo);
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: modificare questo possibilmente
			// l'eccezione indica un errore nella query al db
			//response.sendError(400);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore del cacchio "+e.toString());
			return;
		}
		
		//TODO: temporaneo
		if(risultati == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ao zi nun ce stanno esami.");
			return;
		}
		
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/RisultatiEsame.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("risultati", risultati);
		ctx.setVariable("campo", campo);
		ctx.setVariable("ordine", ordine.equals("ASC") ? "DESC" : "ASC");
		templateEngine.process(path, ctx, response.getWriter());		
	}

	private List<Esaminazione> getRisultatiEsamiByUserRole(User user, int idEsame, String ordine, String campo) throws SQLException{
		List<Esaminazione> risultati = null;
		
		EsameDAO esameDao = new EsameDAO(connection);
		if(user.getRuolo().equals("teacher"))
			risultati = esameDao.getRisultatiEsameProfessore(idEsame, ordine, campo);
		else if(user.getRuolo().equals("student"))
			risultati = esameDao.getRisultatiEsameStudente(user.getMatricola(), idEsame);
		return risultati;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

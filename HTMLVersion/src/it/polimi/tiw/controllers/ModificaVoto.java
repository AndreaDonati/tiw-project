package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/modificaVoto")
public class ModificaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public ModificaVoto() {
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// prendo le credenziali dalla richiesta
		int idEsame;
		String matricolaStudente;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			matricolaStudente = request.getParameter("matricolaStudente");
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Identificativo dell'esame o dell'utente errato.");
			return;
		}
				
		// recupero lo studente
		UserDAO userDao = new UserDAO(connection);
		User studente = null;
		try {
			studente = userDao.getUserFromMatricolaAndExam(matricolaStudente, idEsame);
			if(! userDao.controllaDocente(idEsame, user.getMatricola()))
				// controllo contro web parameters tampering - accesso ad esame di un altro docente
				throw new Exception("Non sei il docente del corso di questo esame.");
			if(studente == null) {
				// controllo contro web parameters tampering - modifica voto di uno studente non registrato
				throw new Exception("Identificativo dello studente errato.");
			}
		} catch (Exception e) {
			//TODO
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
			return;
		}
		
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/Prof/ModificaVoto.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("studente", studente);
		ctx.setVariable("idEsame", idEsame);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

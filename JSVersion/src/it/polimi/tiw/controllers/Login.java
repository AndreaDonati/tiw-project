package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.lang.StringEscapeUtils;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.WebContext;
//import org.thymeleaf.templatemode.TemplateMode;
//import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Login")
@MultipartConfig
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Login() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// prendo le credenziali dalla richiesta
		String usrn = null;
		String pwd = null;
		try {
			usrn = request.getParameter("username");
			pwd = request.getParameter("pwd");
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Credenziali vuote o mancanti.");
			}

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"errorMessage\":\"Credenziali vuote o mancanti.\"}");
			return;
		}

		// controllo le credenziali dello user per l'autenticazione
		// se lo user esiste, lo redireziono alla home
		// altrimenti mando un messaggio di errore
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkCredentials(usrn, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}

		// Se l'utente esiste aggiungo info alla session e reindirizzo alla home page, altrimenti
		// mostro la pagina di login con un messaggio di errore
		
		// Controllo se lo user che si è loggato è uno studente o un professore e lo reindirizzo 
		// alla home page corrispondende
		String path="";
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"errorMessage\":\"Matricola o password non corretti.\"}");
			return;
		} else {
			request.getSession().setAttribute("user", user);
			
			// seleziono il path corretto della home in base al ruolo dello user
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		} 
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
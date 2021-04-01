package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
	private TemplateEngine templateEngine;

	public Login() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// prendo le credenziali dalla richiesta
		String usrn = null;
		String pwd = null;
		try {
//			usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
//			pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
			usrn = request.getParameter("username");
			pwd = request.getParameter("pwd");
			System.out.println(usrn+" - "+pwd);
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Credenziali vuote o mancanti.");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			// redireziono il client a una pagina con lo stesso template del login
			// ma con in aggiunta il messaggio d'errore da mostrare all'utente
			String path;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Credenziali vuote o mancanti.");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
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

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message
		
		// Controllo se lo user che si � loggato � uno studente o un professore e lo reindirizzo 
		// alla home corrispondende allo studente o al professore
		String path="";
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Matricola o password non corretti.");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		} else {
			request.getSession().setAttribute("user", user);
			// seleziono il path corretto in base al ruolo dello user
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
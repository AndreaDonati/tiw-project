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
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/HomeStudente")
public class GoToHomePageStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHomePageStudente() {
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
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		User user = (User) session.getAttribute("user");
//		PROVA + temporaneo
		session.setAttribute("username", user.getUsername());
		session.setAttribute("lastAccessedTime", new java.util.Date());
//		---------------
		List<Corso> corsi = null;
		try {
			CorsoDAO corsoDao = new CorsoDAO(connection);
			corsi = corsoDao.getCorsiFromMatricolaStudente(((User) session.getAttribute("user")).getMatricola());
		} catch (SQLException e) {
			System.out.println(e.toString());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover all users"+e.toString());
			return;
		}
		
		List<List<Esame>> corsiEsami = new ArrayList<List<Esame>>();
		// QUESTA E' UNA PROVA KEKW
		try {
			EsameDAO esameDao = new EsameDAO(connection);
			for (Corso c : corsi) {
				corsiEsami.add(esameDao.getEsamiFromCorso(c.getId()));
			}
		} catch (SQLException e) {
			System.out.println(e.toString());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover all users"+e.toString());
			return;
		}

		
//		for (Corso corso : corsoEsameMap.keySet()) {
//			List<Esame> esami = corsoEsameMap.get(corso);
//			System.out.println("Corso: "+corso.getNome()+" - ");
//			for (Esame es : esami) {
//				System.out.println(es.getDataAppello());
//			}
//		}
		

		// Redirect to the Home page and add missions to the parameters
		String path = "/Templates/Login/Home.html";
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

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

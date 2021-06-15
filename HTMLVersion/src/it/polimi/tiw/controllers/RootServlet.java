package it.polimi.tiw.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("")
public class RootServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public RootServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// controllo se lo user è già loggato o deve ancora loggarsi
		String homepath = request.getServletContext().getContextPath() + "/Home";
		HttpSession session = request.getSession();
		if (!(session.isNew() || session.getAttribute("user") == null)) {
			response.sendRedirect(homepath);
			return;
		}
		
		// default redirect
		String path = request.getServletContext().getContextPath() + "/index.html";
		response.sendRedirect(path);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

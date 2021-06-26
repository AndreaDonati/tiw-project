package it.polimi.tiw.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.beans.User;

/**
 * Servlet implementation class GetInfoUtente
 */
@WebServlet("/getInfoUtente")
public class GetInfoUtente extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetInfoUtente() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		User utente = (User) request.getSession().getAttribute("user");
		
		Gson gson = new Gson();
		
		String jsonObj = gson.toJson(utente);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonObj);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

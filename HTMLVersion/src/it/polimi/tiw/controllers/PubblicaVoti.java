package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/pubblicaVoti")
public class PubblicaVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	
    public PubblicaVoti() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idEsame;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			System.out.println(idEsame);

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"errorMessage\":\"Identificativo dell'esame errato\"}");
			return;
		}
		
		
		// cambio lo stato dei voti relativi all'esame specificato in 'pubblicato'
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		
		try {
			esaminazioneDAO.publishGrades(idEsame);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}


		//TODO: MESSAGGIO DI SUCCESSO?
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

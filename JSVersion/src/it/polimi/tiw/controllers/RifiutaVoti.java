package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.utils.ConnectionHandler;


@WebServlet("/rifiutaVoti")
public class RifiutaVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public RifiutaVoti() {
        super();
    }

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int matricola;
		int idEsame;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			User user = (User) request.getSession().getAttribute("user");
			matricola = user.getMatricola();
			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Identificativo dell'esame o dell'utente errato");
			return;
		}
		
		// cambio lo stato dell'esame specificato in 'rifiutato' per l'utente preso dalla sessione corrente
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		try {
			esaminazioneDAO.rejectGrade(idEsame, matricola);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// redireziono lo user alla visualizzazione del suo voto per l'esame (che sar� cambiata)
		String path = getServletContext().getContextPath() + "/getResults?idEsame="+idEsame;
		response.sendRedirect(path);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.dao.UserDAO;
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int idEsame;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"errorMessage\":\"Identificativo dell'esame errato\"}");
			return;
		}
		
		//controllo che l'utente sia il docente relativo al corso dell'esame
		UserDAO userDAO = new UserDAO(connection);
		try {
			if(!userDAO.controllaDocente(idEsame, user.getMatricola()))
				throw new Exception("L'esame ricercato non esiste o non sei il docente di questo esame.");
		}  catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		} catch (Exception e) {
			// controllo contro web parameters tampering - pubblicazione voti di un esame di un altro docente
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString().replace("java.lang.Exception: ",""));
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


		// redireziono il professore alla pagina con i risultati dell'esame
		String path = getServletContext().getContextPath() + "/getResults?idEsame=" + idEsame;
		response.sendRedirect(path);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

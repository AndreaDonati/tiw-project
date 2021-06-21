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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.MyHttpServlet;


@WebServlet("/rifiutaVoti")
public class RifiutaVoti extends MyHttpServlet {
	private static final long serialVersionUID = 1L;
	
    public RifiutaVoti() {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int matricola;
		int idEsame;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			User user = (User) request.getSession().getAttribute("user");
			matricola = user.getMatricola();
		} catch (Exception e) {
			redirectToErrorPage(request,response, "Identificativo dell'esame o dell'utente errato.");
			return;
		}
		
		
		// cambio lo stato dell'esame specificato in 'rifiutato' per l'utente preso dalla sessione corrente
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		try {
			esaminazioneDAO.rejectGrade(idEsame, matricola);
		} catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
			return;
		}
		
		// redireziono lo user alla visualizzazione del suo voto per l'esame (che sar� cambiata)
		String path = getServletContext().getContextPath() + "/getResults?idEsame="+idEsame;
		response.sendRedirect(path);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

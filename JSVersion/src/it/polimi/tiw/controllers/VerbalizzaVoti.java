package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Verbale;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.dao.VerbaleDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/verbalizzaVoti")
public class VerbalizzaVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public VerbalizzaVoti() {
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
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Identificativo dell'esame errato");
			return;
		}
		
		//controllo che l'utente sia il docente relativo al corso dell'esame
		UserDAO userDAO = new UserDAO(connection);
		try {
			if(!userDAO.controllaDocente(idEsame, user.getMatricola()))
				throw new Exception("L'esame ricercato non esiste o non sei il docente di questo esame.");
		}  catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
			return;
		} catch (Exception e) {
			// controllo contro web parameters tampering - pubblicazione voti di un esame di un altro docente
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString().replace("java.lang.Exception: ",""));
			return;
		}
		
		// cambio lo stato dei voti relativi all'esame specificato in 'verbalizzato'
		// e creo un nuovo verbale relativo all'esame
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		int idVerbale = 0;
		try {
			idVerbale = esaminazioneDAO.recordGrades(idEsame);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		// recupero il verbale appena creato dal db
		VerbaleDAO verbaleDao = new VerbaleDAO(connection);
		Verbale verbale = null;
		try {
			verbale = verbaleDao.getVerbale(idVerbale);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		Gson gson = new Gson();
		
		String jsonObj = gson.toJson(verbale);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonObj);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

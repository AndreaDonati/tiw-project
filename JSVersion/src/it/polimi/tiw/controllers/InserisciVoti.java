package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.utils.ConnectionHandler;


@WebServlet("/inserisciVoti")
@MultipartConfig
public class InserisciVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public InserisciVoti() {
        super();
    }

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int matricolaStudente;
		int idEsame;
		String voto;
		String[] voti = {"", "assente", "rimandato", "riprovato", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30 e Lode"};
		
		try {
			matricolaStudente = Integer.parseInt(request.getParameter("matricola"));
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			voto = request.getParameter("voto");
			
			if(!Arrays.asList(voti).contains(voto))
				throw new Exception();
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Parameri errati");
			return;
		}
		
		
		// inserisco il voto nel db
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		
		try {
			esaminazioneDAO.insertGrade(matricolaStudente, idEsame, voto, user.getMatricola());
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

	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsaminazioneDAO;
import it.polimi.tiw.utils.MyHttpServlet;


@WebServlet("/inserisciVoti")
public class InserisciVoti extends MyHttpServlet {
	private static final long serialVersionUID = 1L;
	
    public InserisciVoti() {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int matricolaStudente;
		int idEsame;
		String voto;
		String[] voti = {"", "assente", "rimandato", "riprovato", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30 e Lode"};
		
		try {
			matricolaStudente = Integer.parseInt(request.getParameter("matricolaStudente"));
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			voto = request.getParameter("voto");
			
			// controllo contro web parameters tampering - inserimento di un voto non valido
			if(!Arrays.asList(voti).contains(voto))
				throw new Exception();

		} catch (Exception e) {
			redirectToErrorPage(request,response, "Parametri errati.");
			return;
		}
		
		
		// inserisco il voto nel db
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);
		
		try {
			esaminazioneDAO.insertGrade(matricolaStudente, idEsame, voto, user.getMatricola());
		} catch (SQLException e) {
			redirectToErrorPage(request,response, e.toString());
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

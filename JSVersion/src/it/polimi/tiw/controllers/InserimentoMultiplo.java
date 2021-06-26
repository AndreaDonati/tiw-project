package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Servlet implementation class InserimentoMultiplo
 */
@WebServlet("/inserimentoMultiplo")
@MultipartConfig
public class InserimentoMultiplo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public InserimentoMultiplo() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int idEsame;
		// recupero parametri dalla richiesta
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(e.toString());
			return;
		}
		
		Map<String,String> matricoleVoti = new HashMap<String, String>();

		List<String> matricole = new ArrayList<String>();
		List<String> voti = new ArrayList<String>();
		Map<String, String[]> allMap = request.getParameterMap();
		for (String key : allMap.keySet()) {
		    String[] strArr = (String[]) allMap.get(key);
		    for (String val : strArr) {
		        if(key.equals("matricola"))
		        	matricole.add(val);
		        if(key.equals("voto")) 
		        	voti.add(val);
		    }
		}
		
		for (int i = 0; i < matricole.size(); i++) {
			if(!voti.get(i).equals(""))
				matricoleVoti.put(matricole.get(i), voti.get(i));
		}
		
		// inserisco il voto nel db
		EsaminazioneDAO esaminazioneDAO = new EsaminazioneDAO(connection);

		for (String matricola : matricoleVoti.keySet()) {
			// inserisco voto nel db
			try {
				esaminazioneDAO.insertGrade(Integer.parseInt(matricola), idEsame, matricoleVoti.get(matricola), user.getMatricola());
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore");
				return;
			}
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

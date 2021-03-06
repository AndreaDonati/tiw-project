package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EsameDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GetResults
 */
@WebServlet("/getResults")
public class GetResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetResults() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//recupero lo user dalla sessione
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// recupero i parametri dalla richiesta
		String idEsameParam;
		int idEsame;
		try {
			idEsameParam = request.getParameter("idEsame");
			if(idEsameParam == null) 
				throw new Exception();
			idEsame = Integer.parseInt(idEsameParam);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Richiesta incompleta. Parametri mancanti.");
			return;
		}
		

		// recupero i risultati dell'esame chiesto dallo user:
		// user == teacher: recupero tutti i risultati dell'esame
		// user == student: recupero il solo risultato dello studente
		List<Esaminazione> risultati;
		try {
			risultati = this.getRisultatiEsamiByUserRole(user, idEsame);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString().replace("java.lang.Exception: ",""));
			return;
		}
		
		// uso json.encode per codificare l'oggetto complesso da mandare 
		// al client come risultato
		
		Gson gson = new Gson();
		Esame esame = null;
		if(user.getRuolo().equals("teacher")) {
			// scorro per settare i "modificabile" e togliere le ripetizioni di "esame"
			esame = risultati.get(0).getEsame();
			if(risultati.get(0).getId() != -1)
				for (Esaminazione esaminazione : risultati) {
					esaminazione.setModificabile();
					esaminazione.setEsame(null);
				}

		} else {
			Esaminazione es = risultati.get(0);
			if(!es.isVisualizzabileByStudente())
				risultati = null;
			else
				es.setRifiutabile();
		}
		
		String jsonObj = gson.toJson(risultati);
		String jsonEsame = gson.toJson(esame);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"ruolo\":"+"\""+user.getRuolo()+"\""+
								  ", \"risultati\": "+jsonObj+
								  ", \"pubblicabili\": "+arePubblicabili(risultati)+
								  ", \"verbalizzabili\": "+areVerbalizzabili(risultati)+
								  ", \"esame\": "+ jsonEsame+"}");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private List<Esaminazione> getRisultatiEsamiByUserRole(User user, int idEsame) throws Exception{
		List<Esaminazione> risultati = null;
		
		EsameDAO esameDao = new EsameDAO(connection);
		UserDAO userDAO = new UserDAO(connection);
		if(user.getRuolo().equals("teacher")) {
			if(userDAO.controllaDocente(idEsame, user.getMatricola()))
				risultati = esameDao.getRisultatiEsameProfessore(idEsame);
			else 
				throw new Exception("L'esame ricercato non esiste o non sei il docente di questo esame.");
		}else if(user.getRuolo().equals("student"))
			if(userDAO.controllaStudente(idEsame, user.getMatricola()))
				risultati = esameDao.getRisultatiEsameStudente(user.getMatricola(), idEsame);
			else
				throw new Exception("L'esame ricercato non esiste o non sei iscritto a questo esame.");
		return risultati;
	}
	
	private boolean areVerbalizzabili(List<Esaminazione> risultati) {
		if(risultati == null || risultati.get(0).getId() == -1)
			return false;
		
		for (Esaminazione esaminazione : risultati) {
			if(esaminazione.isVerbalizzabile())
				return true;
		}
		return false;
	}

	private boolean arePubblicabili(List<Esaminazione> risultati) {
		if(risultati == null || risultati.get(0).getId() == -1)
			return false;
		
		for (Esaminazione esaminazione : risultati) {
			if(esaminazione.isPubblicabile())
				return true;
		}
		return false;
	}

	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

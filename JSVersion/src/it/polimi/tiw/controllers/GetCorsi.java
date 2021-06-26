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

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CorsoDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GetCorti
 */
@WebServlet("/getCorsi")
public class GetCorsi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetCorsi() {
        super();
    }

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		/**
		 * La homepage mostra, sia per Studente sia per Professore, gli stessi contenuti
		 * cio� una lista di corsi e per ogni corso una lista di esami.
		 */
		
		// recupero i corsi associati allo user
		// se lo user � Studente: i corsi che frequenta
		// se lo user � Profesore: i corsi che insegna
		List<Corso> corsi = null;
		try {
			corsi = this.getCorsiContentByUserRole(user);
		} catch (SQLException e) {
			// se l'eccezione non è data da un set vuoto di risultati viene loggata e viene
			// mostrata una pagina di errore, altrimenti viene gestita internamente
			if(e.getSQLState() != "S1000") {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Errore");
				return;
			}
		}
		
		Gson gson = new Gson();
		
		String jsonObj = gson.toJson(corsi);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonObj);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private List<Corso> getCorsiContentByUserRole(User user) throws SQLException{
		List<Corso> corsi = null;
		// nelle righe seguenti viene fatta un'interrogazione al db che pu�
		// lanciare una SQLException, la gestione dell'eccezione viene fatta
		// dal chiamante di questo metodo
		CorsoDAO corsoDao = new CorsoDAO(connection);
		if(user.getRuolo().equals("teacher"))
			corsi = corsoDao.getCorsiFromMatricolaProfessore(user.getMatricola());
		else if(user.getRuolo().equals("student"))
			corsi = corsoDao.getCorsiFromMatricolaStudente(user.getMatricola());
		return corsi;
	}
	
	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

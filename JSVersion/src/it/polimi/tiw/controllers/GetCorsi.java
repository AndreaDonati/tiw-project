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

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCorsi() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
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
		List<Corso> corsi;
		try {
			corsi = this.getCorsiContentByUserRole(user);
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: modificare questo possibilmente
			// l'eccezione indica un errore nella query al db
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
			return;
		}
		
		Gson gson = new Gson();
		
		String jsonObj = gson.toJson(corsi);
		System.out.println(jsonObj);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonObj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private List<Corso> getCorsiContentByUserRole(User user) throws SQLException{
		List<Corso> corsi = null;
		// nelle righe seguenti viene fatta un'interrogazione al db che pu�
		// lanciare una SQLException, la gestione dell'eccezione viene fatta
		// dal chiamante di questo metodo
		//TODO: decidere se implementarlo cos� opppure differenziare nel DAO
		CorsoDAO corsoDao = new CorsoDAO(connection);
		if(user.getRuolo().equals("teacher"))
			corsi = corsoDao.getCorsiFromMatricolaProfessore(user.getMatricola());
		else if(user.getRuolo().equals("student"))
			corsi = corsoDao.getCorsiFromMatricolaStudente(user.getMatricola());
		return corsi;
	}
	
}
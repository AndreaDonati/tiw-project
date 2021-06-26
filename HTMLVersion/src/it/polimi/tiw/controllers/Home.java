package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CorsoDAO;
import it.polimi.tiw.utils.MyHttpServlet;

@WebServlet("/Home")
public class Home extends MyHttpServlet {
	private static final long serialVersionUID = 1L;

	public Home() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		/**
		 * La homepage mostra, sia per Studente sia per Professore, gli stessi contenuti
		 * cioè una lista di corsi e per ogni corso una lista di esami.
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
				redirectToErrorPage(request,response, e.toString());
				return;
			}
		}
				
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("allCorsi", corsi);
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// interroga il db per ottenere la lista di corsi insegnati o frequentati dall'utente
	private List<Corso> getCorsiContentByUserRole(User user) throws SQLException{
		List<Corso> corsi = null;

		CorsoDAO corsoDao = new CorsoDAO(connection);
		if(user.getRuolo().equals("teacher"))
			corsi = corsoDao.getCorsiFromMatricolaProfessore(user.getMatricola());
		else if(user.getRuolo().equals("student"))
			corsi = corsoDao.getCorsiFromMatricolaStudente(user.getMatricola());
		return corsi;
	}

}

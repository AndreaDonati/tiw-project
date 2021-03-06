package it.polimi.tiw.controllers;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.MyHttpServlet;

@WebServlet("/modificaVoto")
public class ModificaVoto extends MyHttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ModificaVoto() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// prendo le credenziali dalla richiesta
		int idEsame;
		String matricolaStudente;
		
		try {
			idEsame = Integer.parseInt(request.getParameter("idEsame"));
			matricolaStudente = request.getParameter("matricolaStudente");
		} catch (Exception e) {
			redirectToErrorPage(request,response,"Identificativo dell'esame o dello studente errato.");
			return;
		}
				
		// recupero lo studente
		UserDAO userDao = new UserDAO(connection);
		User studente = null;
		try {
			studente = userDao.getUserFromMatricolaAndExam(matricolaStudente, idEsame);
			if(! userDao.controllaDocente(idEsame, user.getMatricola()))
				// controllo contro web parameters tampering - accesso ad esame di un altro docente
				throw new Exception("L'esame ricercato non esiste o non sei il docente di questo esame.");
			if(studente == null) {
				// controllo contro web parameters tampering - modifica voto di uno studente non registrato
				throw new Exception("Identificativo dello studente errato.");
			}
		} catch (Exception e) {
			redirectToErrorPage(request,response,e.toString().replace("java.lang.Exception: ",""));
			return;
		}
		
		// Indirizza l'utente alla home e aggiunge corsi e corrispondenza corsi-esami ai parametri
		String path = "/Templates/Prof/ModificaVoto.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("studente", studente);
		ctx.setVariable("idEsame", idEsame);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

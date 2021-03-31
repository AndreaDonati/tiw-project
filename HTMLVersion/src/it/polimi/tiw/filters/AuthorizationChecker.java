package it.polimi.tiw.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.dao.CorsoDAO;
import it.polimi.tiw.utils.ConnectionHandler;
/**
 * Servlet Filter implementation class AuthorizationChecker
 */
@WebFilter("/AuthorizationChecker")
public class AuthorizationChecker implements Filter {
	
    /**
     * Default constructor. 
     */
    public AuthorizationChecker() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {		
		// casting necessario delle variabili
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		// recupero lo user dalla sessione
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
				
		// controllo che lo user sia un professore
		// se non lo e', redirecto alla pagina di login
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		if(!user.getRuolo().equals("teacher")) {
			resp.sendRedirect(loginpath);
			return;
		}
		
		// recupero i corsi insegnati dal professore
		CorsoDAO corsoDao = new CorsoDAO(ConnectionHandler.getConnection(req.getServletContext()));
		List<Corso> corsiInsegnati = null;
		try {
			corsiInsegnati = corsoDao.getCorsiFromMatricolaProfessore(user.getMatricola());
		} catch (SQLException e) {
			System.out.println("qualcosa e' andato molto storto.");
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		// cerco se tra i corsiInsegnati c'e'  il corso a cui 
		// corrisponde l'esame di cui sta modificando il voto
		//TODO: se questo parametro non è nella richiesta il server ritorna una pagina di errore, ok o non ok?
		int idCorso = Integer.parseInt(req.getParameter("idCorso"));
		if(!checkIdCorsoInCorsiInsegnati(idCorso, corsiInsegnati)) {
			System.out.println("Professore non autorizzato.");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	private boolean checkIdCorsoInCorsiInsegnati(int idCorso, List<Corso> corsiInsegnati) {
		for (Corso corso : corsiInsegnati) {
			if(corso.getId() == idCorso)
				return true;
		}
		return false;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

package it.polimi.tiw.filters;

import java.io.IOException;
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

/**
 * Servlet Filter implementation class StudentChecker
 */
@WebFilter("/StudentChecker")
public class StudentChecker implements Filter {

    /**
     * Default constructor. 
     */
    public StudentChecker() {
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
		
		// Controllo che esista una sessione attiva
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		if (user == null) {
			resp.sendRedirect(loginpath);
			return;
		}
		
		
		// controllo che lo user sia uno studente
		// se non lo �, redirecto alla pagina di login
		if(!user.getRuolo().equals("student")) {
			resp.sendRedirect(loginpath);
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

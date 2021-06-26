package it.polimi.tiw.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;

/**
 * Servlet Filter implementation class StudentChecker
 */
public class StudentChecker implements Filter {

    public StudentChecker() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// casting necessario delle variabili
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		// recupero lo user dalla sessione
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
		// controllo che lo user sia uno studente
		// se non lo e', redirecto alla pagina di login
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		if(!user.getRuolo().equals("student")) {
			resp.sendRedirect(loginpath);
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {}

	public void destroy() {}
}

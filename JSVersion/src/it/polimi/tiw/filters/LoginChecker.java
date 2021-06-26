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

/**
 * Servlet Filter implementation class LoginChecker
 */
public class LoginChecker implements Filter {

    public LoginChecker() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// casting necessario delle variabili
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		// controllo se lo user � gi� loggato o deve ancora loggarsi
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		HttpSession session = req.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			resp.sendRedirect(loginpath);
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {}

	public void destroy() {}

}

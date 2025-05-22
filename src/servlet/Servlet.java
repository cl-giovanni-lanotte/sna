package servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * la classe Servlet ha un solo metodo per cancellare la cashe del browser, che viene ereditato all'interno delle classi HomeServlet, LoginServlet, LogoutServlet, NewProjectServlet e RegistrationServlet
 * @author giovanni
 *
 */
public class Servlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * il metoso disableCache serve per cancellare la cashe del browser.
	 * @param res
	 * @return
	 */
	protected final HttpServletResponse disableCache(HttpServletResponse res) {
		res.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
		res.setHeader("Pragma","no-cache");
		res.setDateHeader("Expires", 0);
    	return res;
	}
}

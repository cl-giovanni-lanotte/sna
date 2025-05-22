package servlet;
import Cookies.CookiesContainer;
import database.DataBaseSicurezza;
import exception.DatabaseConnectionException;
import exception.InvalidCookiesException;
import project.Progects;
import user.Users;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.errorprone.annotations.Immutable;

@Immutable
@WebServlet("/home")
/**
 * La classe HomeServerlet serve per visualizzare l'email, il profilo e tutti i progetti presenti all'interno del database.
 * @author giovanni
 *
 */
public final class HomeServerlet extends Servlet {
    private static final long serialVersionUID = 1L;

    public HomeServerlet() {
        super();
    }
    /**
     * il metodo doGet, viene chiamata quando c'e' una richiesta di tipo get da parte di un Browser, controllando se i cookie sono corretti visualizzando la home, altrimenti inoltra alla pagina di login.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response =disableCache(response);
		Cookie[] cookies = request.getCookies();
		boolean cid = false;
		if (cookies == null || cookies.length == 0) {
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} else {
			try {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("id")) {
						try {
							//controllo la validità del cookie
							cid = true;
							String id = cookie.getValue();
							DataBaseSicurezza db = new DataBaseSicurezza();
							db.initConnection();
							CookiesContainer element = db.isCookie(id);
							Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(element.getDate());
							Instant instant = Instant.now();
							Date nowDate = (Date) Date.from(instant);
							if (nowDate.compareTo(d) > 0) {
								//cookie  non valido passa all'interfaccia login
								cookie.setMaxAge(0);
								response.addCookie(cookie);
								request.getRequestDispatcher("login.jsp").forward(request, response);
							} else {
								//cookie valido allora creo la pagina dinamica contenente tutti i vari progetti
								Users user = new Users(element);
								createWebPage(user, request, response);
							}
						} catch (InvalidCookiesException e) {
							cookie.setMaxAge(0);
							response.addCookie(cookie);
							request.getRequestDispatcher("login.jsp").forward(request, response);
						} 
					}
				}
			} catch (DatabaseConnectionException | SQLException | ParseException e) {
				PrintWriter out = response.getWriter();
				out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
						+ "<p> Errore connessione al database.</p>\n"
						+ "<form method = \"get\" action=\"index.html\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
				out.close();

			} 
		}
		if (!cid)
			//cookie non è presente quindi passa all'interfaccia login
			request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	/**
	 * il metodo doPost prende gli attributi passati dalla classe login e restituirà la pagina home contenente l'emil, l'immagine di profilo e tutte le richieste di progetto.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response =disableCache(response);
		Users u = (Users) request.getAttribute("utente");
		try {
			createWebPage(u, request, response);
		} catch (SQLException|DatabaseConnectionException throwables) {
			PrintWriter out = response.getWriter();
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p> Errore connessione al database.</p>\n"
					+ "<form method = \"get\" action=\"index.html\">\n"
					+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			out.close();
		} 

	}
/**
 * il metodo createWebPage crea la pagina contenente l'emil dell'utente che ha fatto l'accesso, l'immagine del profilo e tutte le richieste di progetto.
 * @param utente che ha fatto l'accesso
 * @param request contiene tutte le informazioni peer il servlet
 * @param response serve per inviare la pagina web dinamica che e' stata creata.
 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
 */
	private void createWebPage(Users utente, HttpServletRequest request, HttpServletResponse response)
			throws IOException, SQLException, DatabaseConnectionException {
		PrintWriter out = response.getWriter();
		out.println("<html>\n" + "<head>\n" + "    <title>Home</title>\n" + "</head>\n" + "<style>\n"
				+ "table, th, td {\n" + "  border: 1px solid black;\n" + "}\n" + "</style>\n" + "<body>\n"
				+ "<a href='/sna/logout'>logout</a><br>\n" + "<b1>Ciao " + utente.getEmail() + "</b1>\n"
				+ "<div class=\"col-md-3\">\n" + "    <img src='" + utente.getImage() + "' />\n" + "</div>\n"
				+ "<a href='/sna/InsertProject'>Inserisci la tua proposta</a>\n" + "<br><br>\n");

		out.println("<table>\n" + "<tr>\n" + "<th width='20%'>Autore</th>\n" + "<th width='20%'>Titolo Progetto</th>\n"
				+ "<th width='55%'>Descrizione</th>\n" + "<th width='5%'>Download</th>\n" + "</tr>\n");
		ArrayList<Progects> progetti = utente.getProgetti();
		for (Progects p : progetti) {
			out.println("<tr>\n" + "<th width='20%'>" + p.getEmail() + "</th>\n" + "<th width='20%'>" + p.getTitle()
					+ "</th>\n" + "<th width='55%'>" + p.getDescription().replaceAll("\n", "<br>") + "</th>\n"
					+ "<th width='5%'><a href='" + p.getPath() + "' download>Download</a></th>\n" + "</tr>\n");
		}
		out.println("</table>\n" + "</body>\n" + "</html>");
		out.close();

	}
}

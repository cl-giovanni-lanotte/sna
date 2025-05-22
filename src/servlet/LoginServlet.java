package servlet;

import control.Controls;
import database.DataBaseSicurezza;
import exception.DatabaseConnectionException;
import exception.ErrorEmailException;
import exception.InvalidCookiesException;
import exception.PasswordException;
import user.Users;
import Cookies.CookiesContainer;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.errorprone.annotations.Immutable;

@WebServlet("/index.html")
@MultipartConfig
@Immutable
/**
 * la classe loginServlet serve per far inserire all'utente l'email e la password e dare la possibilità di eseguire un accesso automatico tramite cookie.
 * @author giovanni
 *
 */
public final class LoginServlet extends Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * il metodo doGet viene utilizzato quando ci sono richieste get da parte del browser, controlla se e' presente un cookie di sessione in modo da far passare direttamente alla home.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response = disableCache(response);
		Cookie[] cookies = request.getCookies();
		boolean cid = false;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				//controllo che esiste
				if (cookie.getName().equals("id")) {
					cid = true;
					String id = cookie.getValue();
					DataBaseSicurezza db = new DataBaseSicurezza();
					try {
						//controllo la validita del cookie
						db.initConnection();
						CookiesContainer element = db.isCookie(id);
						Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(element.getDate());
						Instant instant = Instant.now();
						Date nowDate = (Date) Date.from(instant);
						if (nowDate.compareTo(d) > 0) {
							//cookie non valido passa alla fase di login
							cookie.setMaxAge(0);
							response.addCookie(cookie);
							request.getRequestDispatcher("login.jsp").forward(request, response);
						} else {
							//cookie valido va alla home
							request.getRequestDispatcher("/home").include(request, response);
						}
					} catch (DatabaseConnectionException | SQLException | ParseException e) {
						e.printStackTrace();
					} catch (InvalidCookiesException e) {
						cookie.setMaxAge(0);
						response.addCookie(cookie);
						request.getRequestDispatcher("login.jsp").forward(request, response);
					}

				}
			}
		}
		if (!cid)
			//se il browser non contiene il cookie indicato passa alla fase di login
			request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	/**
	 * il metodo doPost, viene richianata quando abbiamo la richiesta del metodo post da parte del browser, che controlla l'email e la password in modo da controllare l'esistenza dell'utente e passare la richiesta alla servlet home. 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response = disableCache(response);
		String email = request.getParameter("email");
		byte[] password = request.getParameter("password").getBytes();
		String[] checkBox = request.getParameterValues("cookies");
		if (email.isEmpty() || password.length == 0 || !Controls.controlEmail(email)
				|| !Controls.controlPassword(password)) {
			// L'email o la password contengono carattere illegali
			Arrays.fill(password, (byte) '0');
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} else {
			boolean cookies;
			boolean isCookies = false;
			Cookie[] cookiesBrowser = request.getCookies();
			// controllo se il browser contiene il cookie id
			for (Cookie c : cookiesBrowser) {
				if (c.getName().equals("id")) {
					response.sendRedirect("/sna/");
					isCookies = true;
				}
			}
			//controllo della checkbox
			if (!isCookies) {
				if (checkBox != null) {
					cookies = checkBox[0].equals("on");

				} else
					cookies = false;
				try {
					//creazione dell'utente con il relativo cookie
					Users user = Users.login(email, password, cookies);
					Arrays.fill(password, (byte) '0');
					if (cookies) {
						Cookie cookie = new Cookie("id", user.getCookieElement().getId());
						cookie.setHttpOnly(true);
						cookie.setMaxAge(1200);
						response.addCookie(cookie);
					}
					//passaggio dei paranetri alla serverlet home.
					request.setAttribute("utente", user);
					request.getRequestDispatcher("/home").include(request, response);
				} catch (NoSuchAlgorithmException e) {
					out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
							+ "<p>Errore sulla scelta dell'agoritmo di crittografia.</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
				} catch (SQLException | DatabaseConnectionException throwables) {
					out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
							+ "<p> Errore connessione al database.</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
				} catch (PasswordException e) {
					out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
							+ "<p> La password dell'email e' sbagliata.</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
				} catch (ErrorEmailException e) {
					out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
							+ "<p> La e-mail inserita e' sbagliata.</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
					
				}finally {
					Arrays.fill(password, (byte) '0');
					out.close();
				}

			}

		}

	}

}
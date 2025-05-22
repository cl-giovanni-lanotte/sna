package servlet;

import Cookies.CookiesContainer;
import control.Controls;
import control.Download;
import database.DataBaseSicurezza;
import exception.DatabaseConnectionException;
import exception.ErrorEmailException;
import exception.ContentFileException;
import exception.CookieExpiredException;
import exception.FileTypeException;
import exception.InvalidCookiesException;
import exception.PasswordException;
import user.Users;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.google.errorprone.annotations.Immutable;

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
import javax.servlet.http.*;


@WebServlet("/InsertProject")
@MultipartConfig
@Immutable
/**
 * la classe NewProjectServlet serve per inserire un nuovo progetto da parte dell'utente.
 * @author giovanni
 *
 */
public final class NewProjectServlet extends Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NewProjectServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * il metodo doPost serve per gestire richieste Post da parte di un browser, in questo caso controllerà tutti i parametri e in caso positivo inserira' il progetto all'interno del database altriementi visualizzerà i messaggi d'errore.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response = disableCache(response);
		String titolo = request.getParameter("titolo");
		Part file = request.getPart("file");
		Cookie[] cookies = request.getCookies();
		boolean cid = false;
		PrintWriter out = response.getWriter();
		try {
			if (cookies == null || cookies.length == 0) {
				request.getRequestDispatcher("login.jsp").forward(request, response);
			} else {
				for (Cookie cookie : cookies) {
					try {
						if (cookie.getName().equals("id")) {
							// controllo la valiodità del cookie
							cid = true;
							String id = cookie.getValue();
							DataBaseSicurezza db = new DataBaseSicurezza();
							db.initConnection();
							CookiesContainer element = db.isCookie(id);
							Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(element.getDate());
							Instant instant = Instant.now();
							Date nowDate = (Date) Date.from(instant);
							if (nowDate.compareTo(d) > 0) {
								// cookie non valido passa viene cancellato e visuallizza l'errore di coockie
								// scaduto
								cookie.setMaxAge(0);
								response.addCookie(cookie);
								throw new CookieExpiredException();
							} else {
								// cookie valido passa all'inserimento del progetto
								Users user = new Users(element);
								insertProject(request, response, user, titolo, file, true);

							}
						}
					} catch (InvalidCookiesException e) {
						cookie.setMaxAge(0);
						response.addCookie(cookie);
						request.getRequestDispatcher("/InsertProject").forward(request, response);
					}
				}
				if (!cid) {
					// se il browser non contiene il cookie indicato allora controllerà pure l'email
					// e la password
					String email = request.getParameter("email");
					byte[] password = request.getParameter("password").getBytes();
					try {
						if (!Controls.controlEmail(email) && Controls.controlPassword(password)) {
							request.getRequestDispatcher("projectInsertWithLogin.jsp").forward(request, response);
						}
						Users user = Users.login(email, password, false);
						Arrays.fill(password, (byte) '0');
						insertProject(request, response, user, titolo, file, false);
					} finally {
						Arrays.fill(password, (byte) '0');
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p>Errore sulla scelta dell'agoritmo di crittografia.</p>\n"
					+ "<form method = \"get\" action=\"InsertProject\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>");
			out.close();
		} catch (SQLException | DatabaseConnectionException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p> Errore connessione al database.</p>\n" + "<form method = \"get\" action=\"InsertProject\">\n"
					+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			out.close();
		} catch (PasswordException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p> La password dell'email e' sbagliata.</p>\n"
					+ "<form method = \"get\" action=\"InsertProject\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>");
			out.close();
		} catch (TikaException | SAXException e) {
			e.printStackTrace();
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p>Errore durante il controllo del file, riprovare.</p>\n"
					+ "<form method = \"get\" action=\"InsertProject\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>");
			out.close();
		} catch (ErrorEmailException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p> La e-mail inserita e' sbagliata.</p>\n"
					+ "<form method = \"get\" action=\"InsertProject\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>");
			out.close();
		} catch (FileTypeException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p>Il file non e' di tipo TXT</p>\n" + "<form method = \"get\" action=\"InsertProject\">\n"
					+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>"

			);
		} catch (ParseException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p>C'è stato un errore sul calcolare la data di pubblicazione del progetto, riprovare.</p>\n"
					+ "<form method = \"get\" action=\"InsertProject\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>"

			);
		} catch (ContentFileException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p>Il file contiene script malevoli.</p>\n"
					+ "<form method = \"get\" action=\"InsertProject\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>");
		} catch (CookieExpiredException e) {
			out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
					+ "<p>Il cookie e' scaduto, inserisci le credenziali.</p>\n"
					+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			out.close();
		}
	}
	/**
	 * il metodo insertProgect controlla se il titolo e il file sono corretti, nel caso positivo inserirà il progetto altrimenti visualizzerà le finestri di inserimento del progetto.
	 * @param request contiene tutte le informazioni peer il servlet
	 * @param response serve per inviare la pagina web dinamica che e' stata creata.
	 * @param user e' la classe dove e' presente i dati dell'utente
	 * @param titolo il titolo della richiesta di progetto
	 * @param file il file della richiesta del progetto
	 * @param cookie un boolean che indica la presenza o meno del cookie.
	 * @throws ServletException Definisce un'eccezione generale che un servlet può lanciare quando incontra difficoltà.
	 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 * @throws TikaException solleva l'eccezione quando ci sono problemi ad controllare i metadati.
	 * @throws SAXException solleva l'eccezione quando ci sono problemi nel calcolo del parse relativo al metadata.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws FileTypeException si solleva per indicare quando il file non e' di tipo TXT.
	 * @throws ContentFileException il contenuto del file TXT contiene codice malevolo.
	 */
	private void insertProject(HttpServletRequest request, HttpServletResponse response, Users user, String titolo,
			Part file, boolean cookie)
			throws ServletException, IOException, TikaException, SAXException, DatabaseConnectionException,
			SQLException, FileTypeException, ContentFileException {
		// controllo senza cookies
		if (!isCorrect(titolo, file)) {
			if (cookie) {
				request.getRequestDispatcher("projectInsertWithCookie.jsp").forward(request, response);
			} else {
				request.getRequestDispatcher("projectInsertWithLogin.jsp").forward(request, response);
			}
		} else {
			insertDBProject(titolo, file, user);
			PrintWriter out = response.getWriter();
			out.println("<html>\n" + "<head>\n" + "  <title>Inserimento completato</title>\n" + "</head>\n" + "\n"
					+ "<body>\n" + "<p>Il tuo progetto è stato inserito con successo</p>\n"
					+ "<form method = \"get\" action=\"index.html\">\n" + "<input type = \"submit\" value = \"Ok\">"
					+ "</form>" + "</body>\n" + "</html>");
			out.close();
		}

	}
	/**
	 * il metodo isCorrect controlla il file e il titolo, restituisce true se il file e il testo sono corretti altrimenti false.
	 * @param title il titolo della richiesta di progetto
	 * @param file il file della richiesta del progetto
	 * @return restituisce true se il file e il testo sono corretti altrimenti false
	 * @throws TikaException solleva l'eccezione quando ci sono problemi ad controllare i metadati.
	 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 * @throws SAXException solleva l'eccezione quando ci sono problemi nel calcolo del parse relativo al metadata.
	 * @throws FileTypeException si solleva per indicare quando il file non e' di tipo TXT.
	 * @throws ContentFileException il contenuto del file TXT contiene codice malevolo.
	 */
	private boolean isCorrect(String title, Part file)
			throws TikaException, IOException, SAXException, FileTypeException, ContentFileException {
		Download fileDownload = new Download(file);
		if (fileDownload.isTxt())
			return Controls.controlTitolo(title);
		else
			throw new FileTypeException("il file non è un txt");
	}
	/**
	 * il metodo insertDBProject salva il nome del file e il titolo del progetto all'interno del database.
	 * @param title il titolo della richiesta di progetto
	 * @param file il file della richiesta del progetto
	 * @param user e' la classe dove e' presente i dati dell'utente
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws ContentFileException il contenuto del file TXT contiene codice malevolo.
	 * @throws FileTypeException si solleva per indicare quando il file non e' di tipo TXT.
	 * @throws TikaException solleva l'eccezione quando ci sono problemi ad controllare i metadati.
	 * @throws SAXException solleva l'eccezione quando ci sono problemi nel calcolo del parse relativo al metadata.
	 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 */
	private void insertDBProject(String title, Part file, Users user) throws DatabaseConnectionException, SQLException,
			ContentFileException, FileTypeException, TikaException, SAXException, IOException {
		user.saveProject(file, title);
	}

	/**
	 * il metodo doGet serve per gestire le richieste di tipo get da parte del browser, visualizzerà la schermata di visualizzazione in base alla presenza dell'id di sessione.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response = disableCache(response);
		Cookie[] cookies = request.getCookies();
		boolean cid = false;
		if (cookies == null || cookies.length == 0) {
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} else {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("id")) {
					// controllo la validità del cookie
					cid = true;
					String id = cookie.getValue();
					DataBaseSicurezza db = new DataBaseSicurezza();
					try {
						db.initConnection();
						CookiesContainer element = db.isCookie(id);
						Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(element.getDate());
						Instant instant = Instant.now();
						Date nowDate = (Date) Date.from(instant);
						if (nowDate.compareTo(d) > 0) {
							// cookie non valido passa all'interfaccia projectInsertWithLogin
							cookie.setMaxAge(0);
							response.addCookie(cookie);
							request.getRequestDispatcher("projectInsertWithLogin.jsp").include(request, response);
						} else {
							// cookie valido passa all'interfaccia projectInsertWithCookie
							request.getRequestDispatcher("projectInsertWithCookie.jsp").forward(request, response);
						}
					} catch (DatabaseConnectionException | SQLException | ParseException e) {
						e.printStackTrace();
					} catch (InvalidCookiesException e) {

						cookie.setMaxAge(0);
						response.addCookie(cookie);
						request.getRequestDispatcher("/InsertProject").forward(request, response);
					}

				}
			}
		}
		if (!cid)
			// cookie non è presente allora passa all'interfaccia projectInsertWithCookie
			request.getRequestDispatcher("projectInsertWithLogin.jsp").forward(request, response);
	}
}
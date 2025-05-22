package servlet;

import control.Controls;
import control.Download;
import exception.DatabaseConnectionException;
import exception.ErrorEmailException;
import exception.FileTypeException;
import exception.PasswordException;
import user.Users;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.google.errorprone.annotations.Immutable;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * La classe RegistrationServlet serve per registrare l'utente all'interno del database.
 */
@WebServlet("/registerUser")
@MultipartConfig
@Immutable
public final class RegistrationServlet extends Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistrationServlet() {
		super();
	}

	/**
	 * il metodo doGet gestisce le richieste get del browser, visualizzera' la pagina html di registrazione.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response = disableCache(response);
		request.getRequestDispatcher("register.jsp").forward(request, response);
	}

	/**
	 * il metodo doPost gestisce le richieste post del browser, controllando tutti i vari parametri inviati dal browser e gestendo la registrazione dell'utente.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response = disableCache(response);
		Part file = request.getPart("file");
		String email = request.getParameter("email");
		byte[] password = request.getParameter("password").getBytes();
		byte[] password2 = request.getParameter("password2").getBytes();
		// Controllo dei vari parametri
		if (email.isEmpty() || password.length == 0 || !Controls.controlEmail(email)
				|| !Controls.controlPassword(password) || !Controls.controlPassword(password2)
				|| !Arrays.equals(password, password2) || !file.getContentType().contains("jpeg")) {
			// l'email, password, password2 o il file contengono dei caratteri illegali
			Arrays.fill(password, (byte) '0');
			Arrays.fill(password2, (byte) '0');
			request.getRequestDispatcher("register.jsp").forward(request, response);
		} else {
			// non contiene caratteri illegali e possiamo inserirli all'interno del DB.
			Arrays.fill(password2, (byte) '0');
			PrintWriter out = response.getWriter();
			try {
				Download fileDownload = new Download(file);
				if (fileDownload.isImagine()) {
					File image = fileDownload.uploadImage();
					new Users(email, password,image);
					Arrays.fill(password, (byte) '0');
					// passare tutti i dati con il post
					// request.getParameter();
					out.println("<html>\n" + "<head>\n" + "  <title>Registrazione Completata</title>\n" + "</head>\n"
							+ "\n" + "<body>\n" + "<p> La registrazione e' completata</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
				} else {
					throw new FileTypeException("Tipo di file non corretto");
				}
			} catch (NoSuchAlgorithmException e) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
						+ "<p>Erroe sulla scelta dell'agoritmo di crittografia.</p>\n"
						+ "<form method = \"get\" action=\"registerUser\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			} catch (SQLException throwables) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore utente esistente</title>\n" + "</head>\n" + "\n"
						+ "<body>\n" + "<p>L'email e' gia' presente nel sistema, effettuare il login</p>\n"
						+ "<form method = \"get\" action=\"index.html\">\n" + "<input type = \"submit\" value = \"Ok\">"
						+ "</form>" + "</body>\n" + "</html>"

				);
			} catch (PasswordException e) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
						+ "<p> La password dell'email e' sbagliata.</p>\n"
						+ "<form method = \"get\" action=\"registerUser\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			} catch (DatabaseConnectionException e) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
						+ "<p> Errore connessione al database.</p>\n"
						+ "<form method = \"get\" action=\"registerUser\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			} catch (FileTypeException e) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore nel file</title>\n" + "</head>\n" + "\n"
						+ "<body>\n" + "<p>Il file non e' un immagine</p>\n"
						+ "<form method = \"get\" action=\"registerUser\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>"
				);
			} catch (TikaException | SAXException e) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
						+ "<p>Errore durante il controllo del file, riprovare.</p>\n"
						+ "<form method = \"get\" action=\"registerUser\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			} catch (ErrorEmailException e) {
				out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
						+ "<p> La e-mail inserita e' sbagliata.</p>\n"
						+ "<form method = \"get\" action=\"registerUser\">\n"
						+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
			}
			finally {
				Arrays.fill(password, (byte) '0');
				out.close();
			}
			

		}

	}

}
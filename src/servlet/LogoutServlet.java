package servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.errorprone.annotations.Immutable;

import database.DataBaseSicurezza;
import exception.DatabaseConnectionException;

/**
 * La classe logout serve per cancellare la sessione salvata nel cookie e nel database.
 */
@WebServlet("/logout")
@Immutable
public final class LogoutServlet extends Servlet {
    private static final long serialVersionUID = 1L;

    public LogoutServlet() {
        super();
    }

    /**
     * il metodo doGet risponde alle richieste get del browser in modo da cancellare id della sessione sia nel browser e sia nel database che contiene id della sessione.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        for( Cookie cookie : cookies){
            if(cookie.getName().equals("id")){
            	try {
            	DataBaseSicurezza db = new DataBaseSicurezza();
            	db.initConnection();
            	db.deleteCookie(cookie.getValue());
            	} catch(SQLException e) {
            		PrintWriter out = response.getWriter();
            		out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
							+ "<p> Errore durante la cancellazione dell'id della sessione.</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
            		out.close();
            	} catch (DatabaseConnectionException e) {
            		PrintWriter out = response.getWriter();
            		out.println("<html>\n" + "<head>\n" + "  <title>Errore</title>\n" + "</head>\n" + "\n" + "<body>\n"
							+ "<p> Errore connessione al database.</p>\n"
							+ "<form method = \"get\" action=\"index.html\">\n"
							+ "<input type = \"submit\" value = \"Ok\">" + "</form>" + "</body>\n" + "</html>");
            		out.close();
				}
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                }
            }
        response.sendRedirect("/sna/");
    }


}
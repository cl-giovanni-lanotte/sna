package user;
import Cookies.CookiesContainer;
import control.Download;
import database.DataBaseSalt;
import database.DataBaseSicurezza;
import exception.DatabaseConnectionException;
import exception.ErrorEmailException;
import exception.PasswordException;
import project.Progects;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.Part;


import com.google.errorprone.annotations.Immutable;
@Immutable
/**
 * La classe User si occupa di gestire la creazione, l'accesso e tutte le varie azioni riguardate l'utente.
 * @author giovanni
 *
 */
public final class Users {
	private String email;
	private CookiesContainer cookieElement = null;
	/**
	 * il metodo di classe login serve per ricavare l'utente tramite dal database.
	 * @param email associata all'utente
	 * @param password associata all'utente
	 * @param cookies è un boolean, se e' true crea pure il id della sessione altrimenti false.
	 * @return restituisce l'oggetto della classe Users.
	 * @throws NoSuchAlgorithmException si solleva quando non riesce a utilizzare l'algoritmo sha indicato
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws PasswordException si solleva quando la password associata all'email non e' corretta.
	 * @throws ErrorEmailException si solleva quando l'email inserita non e' presente nel database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 */
	public static Users login(String email, byte[] password, boolean cookies) throws NoSuchAlgorithmException,
			DatabaseConnectionException, PasswordException, ErrorEmailException, SQLException, IOException {
		Users u = new Users(email);
		u.login(password);
		if (cookies)
			u.createToken();
		return u;
	}
	/**
	 * Crea l'oggetto della classe Users.
	 * @param email email associato all'utente
	 */
	private Users(String email) {
		this.email = email;
	}
	/**
	 * Crea l'oggetto della classe Users e viene utilizzato nella fase di registrazione dell'utente.
	 * @param email associato all'utente
	 * @param password assiciata all'utente
	 * @throws NoSuchAlgorithmException si solleva quando non riesce a utilizzare l'algoritmo sha indicato
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws PasswordException si solleva quando la password associata all'email non e' corretta.
	 * @throws ErrorEmailException si solleva quando l'email inserita non e' presente nel database.
	 */
	public Users(String email, byte[] password, File image) throws NoSuchAlgorithmException, SQLException,
			DatabaseConnectionException, PasswordException, ErrorEmailException {
		this.email = email;
		register(password);
		saveImage(image);
	}
	/**
	 * il costruttore viene usato per creare l'oggetto CookieContainer.
	 * @param cookie contiene tutte le informazioni del cookie, contenute nell'oggetto della classe CookiesContainer.
	 */
	public Users(CookiesContainer cookie) {
		email = cookie.getEmail();
		cookieElement = cookie;
	}
	/**
	 * il metodo getCookieElement viene usato per restituisce il cookie creato nella fase di login.
	 * @return restituisce tutti i dati relativi al cookie.
	 */
	public CookiesContainer getCookieElement() {
		return cookieElement;
	}
	/**
	 * il metodo register viene usato per inserire tutti i dati dell'utente all'interno del database.
	 * @param password associata all'email.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws NoSuchAlgorithmException si solleva quando non riesce a utilizzare l'algoritmo sha indicato.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws ErrorEmailException si solleva quando l'email inserita non e' presente nel database.
	 */
	private void register(byte[] password)
			throws DatabaseConnectionException, NoSuchAlgorithmException, SQLException, ErrorEmailException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		DataBaseSalt dbSalt = new DataBaseSalt();
		dbSalt.initConnection();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		dbSalt.setSalt(this.email);
		byte[] salt = dbSalt.getSalt(email);
		byte[] hashPasswordSalt = digest.digest(appendArray(password, salt));
		Arrays.fill(salt, (byte) '0');
		try {
		db.addElement(this.email, hashPasswordSalt);
		Arrays.fill(hashPasswordSalt, (byte) '0');
		} finally {
			Arrays.fill(hashPasswordSalt, (byte) '0');
		}

	}
	/**
	 * il metodo login viene usato quando vogliamo controllare se l'utente e' presente all'interno del database, nel caso non fossero presenti sollevera' l'eccezione dedicata.
	 * @param password password associata all'email.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws NoSuchAlgorithmException si solleva quando non riesce a utilizzare l'algoritmo sha indicato.
	 * @throws PasswordException si solleva quando la password associata all'email non e' corretta.
	 * @throws ErrorEmailException si solleva quando l'email inserita non e' presente nel database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 */
	private void login(byte[] password) throws DatabaseConnectionException, NoSuchAlgorithmException, PasswordException,
			ErrorEmailException, SQLException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		DataBaseSalt dbSalt = new DataBaseSalt();
		dbSalt.initConnection();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] salt = dbSalt.getSalt(email);
		byte[] hashPasswordSalt = digest.digest(appendArray(password, salt));
		Arrays.fill(salt, (byte) '0');
		try {
			byte[] passwordCorr = db.getPassword(email);
			if (!Arrays.equals(hashPasswordSalt, passwordCorr)) {
				Arrays.fill(hashPasswordSalt, (byte) '0');
				Arrays.fill(passwordCorr, (byte) '0');
				throw new PasswordException("Password non corretta");
			}
			Arrays.fill(hashPasswordSalt, (byte) '0');
			Arrays.fill(passwordCorr, (byte) '0');
		} catch (SQLException e) {
			throw new ErrorEmailException("Email non corretta");
		}
	}
	/**
	 * il metodo createToken serve per creare id di sessione del cookie associato all'utente.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 * @throws NoSuchAlgorithmException si solleva quando non riesce a utilizzare l'algoritmo sha indicato.
	 */
	private void createToken() throws DatabaseConnectionException, SQLException, IOException, NoSuchAlgorithmException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		cookieElement = db.createCookies(email);

	}
	/**
	 * il medoto appendArray serve per concatenare la password con il salt, in modo da formare la passwrod dell'utente.
	 * @param hashPassword e' la password associata all'utente
	 * @param salt e' una stringa che serve per la creazione della password dell'utente
	 * @return restitusce la password dell'utente.
	 */
	private byte[] appendArray(byte[] hashPassword, byte[] salt) {
		byte[] ris = new byte[hashPassword.length + salt.length];
		int i = 0;
		for (; i < hashPassword.length; i++) {
			ris[i] = hashPassword[i];
		}
		for (int k = 0; k < salt.length; k++, i++) {
			ris[i] = salt[k];
		}
		return ris;
	}
	/**
	 * Il metodo saveImage serve per salvare il nome del file all'interno del database.
	 * @param file serve per ricavare il nome del file.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws NoSuchAlgorithmException si solleva quando non riesce a utilizzare l'algoritmo sha indicato.
	 * @throws ErrorEmailException si solleva quando l'email inserita non e' presente nel database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 */
	private void saveImage(File file)
			throws DatabaseConnectionException, NoSuchAlgorithmException, ErrorEmailException, SQLException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		db.setNameFileSave(email, file.getName());
	}
	/**
	 * il metodo getImage restituisce il nome dell'immagine del profilo 
	 * @return il nome dell'immagine del profilo
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 */
	public String getImage() throws SQLException, DatabaseConnectionException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		String url;
		if (cookieElement == null) {
			url = db.getUrlImmage(email);
		} else {
			url = db.getUrlImmage(email, String.valueOf(cookieElement.getId()));
		}
		return Download.DIR_IMAGE + url;
	}
	
	/**
	 * il metodo saveProject serve per salvare il progetto all'interno del server e salvare il nome del file all'interno del database.
	 * @param file il file da salvare all'interno del database.
	 * @param titolo il titolo del progetto.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws IOException si solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 */
	public void saveProject(Part file, String titolo) throws DatabaseConnectionException, SQLException, IOException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		Download fileDownload = new Download(file);
		File fileTxt = fileDownload.uploadTxt();
		db.setProgetto(email, titolo, fileTxt.getName());
	}
	/**
	 * il metodo getProgetti viene utilizzato per restituire i progetti presenti nel database di tutti gli utenti.
	 * @return restituiscce un array di oggetti di tipo Progects.
	 * @throws SQLException si solleva quando ci sono problemi di eseguire il comando SQL.
	 * @throws DatabaseConnectionException si solleva quando ci sono problemi di connessione al database.
	 */
	public ArrayList<Progects> getProgetti() throws SQLException, DatabaseConnectionException {
		DataBaseSicurezza db = new DataBaseSicurezza();
		db.initConnection();
		return db.getProgetti();
	}
	/**
	 * il metodo getEmail restituisce l'email dell'utente.
	 * @return restituisce l'email dell'utente
	 */
	public String getEmail() {
		return email;
	}

}

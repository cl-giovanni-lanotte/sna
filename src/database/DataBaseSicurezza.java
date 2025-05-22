package database;

import Cookies.CookiesContainer;
import control.Controls;
import control.CreateString;
import exception.DatabaseConnectionException;
import exception.ErrorEmailException;
import exception.InvalidCookiesException;
import project.Progects;

import com.google.errorprone.annotations.Immutable;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

@Immutable
/**
 * La classe DataBaseSicurezza serve per connettermi al database dove contiene il salt della password.
 */
public final class DataBaseSicurezza {


    private String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private String DBMS = "jdbc:mysql";
    private String SERVER = "localhost";
    private String DATABASE = "sicurezza";
    private String USER_ID = "SicurezzaUser";
    private String PASSWORD = "sicurezza";

    private Connection conn;

    /**
     * Inizializza una connessione al DB
     */
    public  void initConnection() throws DatabaseConnectionException{
        String connectionString = DBMS+"://" + SERVER + "/" + DATABASE;
        try {

            Class.forName(DRIVER_CLASS_NAME).newInstance();
        }
        catch (ClassNotFoundException e) {
            System.out.println("Impossibile trovare il Driver: " + DRIVER_CLASS_NAME);
            throw new DatabaseConnectionException(e.toString());

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {;
            conn = DriverManager.getConnection(connectionString, USER_ID, PASSWORD);

        } catch (SQLException e) {
            System.out.println("Impossibile connettersi al DB");
            e.printStackTrace();
            throw new DatabaseConnectionException(e.toString());
        }
    }
    /**
     * il metodo addElement serve per inserire l'utente.
     * @param email l'email associato all'utente
     * @param pass la password associato all'utente
     * @return restituiesce true nel caso in cui è stato eseguito con successo l'inserimeto dell'email e della password all'interno del database.
     * @throws SQLException è solleva quando ci sono problemi ad eseguire il comando SQL.
     */
    public boolean addElement(String email, byte[] pass) throws SQLException{
        String query = "insert into utenti (username, password) values (?,?);";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,email);
        statement.setBytes(2,pass);
        statement.executeUpdate();
        query = new String();
        statement.close();
        return true;


    }
    
    /**
     * il metodo getPassword serve per restituire la password associato alla email.
     * @param email l'email associato all'utente
     * @return restituisce la password associato al email.
     * @throws ErrorEmailException solleva questa eccezione quando non e' presente all'interno del database
     * @throws SQLException solleva questa eccezione quando c'è un problema di connessione.
     */
    public byte[] getPassword(String email) throws ErrorEmailException, SQLException {
        Statement statement = conn.createStatement();
        try {
        String query = "select password from utenti where username ='" + email+"';";
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        byte[] pass = rs.getBytes("password");
        statement.close();
        rs.close();
        return pass;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new ErrorEmailException("Email sbagliata");
	    }
       

    }

    /**
     * Il metodo setNameFileSave inserisce il nome del file associato all'email, all'interno del database.
     * @param email email associato all'utente.
     * @param name name il nome del file.
     * @return restituisce true quando viene eseguito tutto il comando altriementi solleva l'eccezione di SQLException
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    public boolean setNameFileSave(String email, String name) throws SQLException {
        String query = "update utenti set url=? where username = ?;";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,name);
        statement.setString(2,email);
        statement.executeUpdate();
        query = new String();
        statement.close();
        return true;

    }

    /**
     * getUrlImmage restituisce il nome del file immagine del profilo.
     * @param email email associatto all'utente.
     * @param token id della sessione associata.
     * @return restituisce il nome dell'immagine.
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    public String getUrlImmage(String email,String token) throws SQLException {
        String query = "select url from utenti join cookies on username=email where id=? and username=? ; ";/*and token = ?;"; */
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,token);
        statement.setString(2,email);
        statement.execute();
        query = "";
        return queryUrlImmage(statement);
    }
    /**
     * getUrlImmage restituisce il nome del file immagine del profilo.
     * @param email email associatto all'utente.
     * @return restituisce il nome dell'immagine.
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    public String getUrlImmage(String email) throws SQLException {
        String query = "select url from utenti where username=? ; ";/*and token = ?;"; */
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,email);
        statement.execute();
        query = "";
        return queryUrlImmage(statement);
    }

    /**
     * il metodo getUrlImmage esegue la query sql passata in input per restitituire il nome dell'immagine.
     * @param statement il risultato della query SQL.
     * @return restituisce il nome dell'immagine.
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    private String queryUrlImmage(Statement statement)throws SQLException{
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        String url = resultSet.getString(1);
        resultSet.close();
        statement.close();
        return url;
    }

    /**
     * il metodo createCookies serve per creare id di sessione per la creazione del cookie.
     * @param email l'email dell'utente
     * @return restituisce l'oggetto CookieContainer che contiene al suo interno id, email e data di scadenza.
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    public CookiesContainer createCookies(String email) throws SQLException {
        String query = "insert into cookies (id,email, dataScadenza) values(?,?,?);";
        PreparedStatement statement = conn.prepareStatement(query);
        CreateString random = new CreateString(50);
        String id = random.createString();
        statement.setString(1,id);
        statement.setString(2,email);
        Instant instant = Instant.now();
        instant = instant.plusSeconds(600);
        Date myDate = (Date) Date.from(instant);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(myDate);
        statement.setString(3,formattedDate);
        statement.executeUpdate();
        query = new String();
        statement.close();
        return new CookiesContainer(id,email, formattedDate);
    }
    
    /**
     * il metodo isCookie controlla se e' presente id di sessione, nel caso negativo solleva l'eccezione di invalidCookiesException.
     * @param id della sessione
     * @return restituisce tutte le informazione della sessione.
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     * @throws InvalidCookiesException l'id della sessione non e' corretta o non presente nel database.
     */
    public CookiesContainer isCookie(String id) throws SQLException, InvalidCookiesException {
    	if(Controls.controlCookie(id)) {
    		throw new InvalidCookiesException();
    	}
    	else {
    		String query = "select email, dataScadenza from cookies where id=?;";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,id);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
    	        String email = resultSet.getString(1);
    	        String data = resultSet.getString(2);
    	        resultSet.close();
    	        statement.close();
    	        return new CookiesContainer(id,email,data);
            }
            else {
            	resultSet.close();
    	        statement.close();
    	        throw new InvalidCookiesException();
            }
    	}     
    }
    /**
     * il metodo deleteCookie elimina l'id della sessione dal database.
     * @param id della sessione in input
     * @return restituisco true se e' corretto.
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    public boolean deleteCookie(String id) throws SQLException {
    	String query = "delete from cookies where id = ?;";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,id);
        statement.executeUpdate();
        query = new String();
        statement.close();
        return true;
    }
    /**
     * il metodo setProgetto inserisce la richiesta di progetto all'interno del database.
     * @param email dell'utente
     * @param titolo della richiesta del progetto
     * @param path il file del progetto
     * @return restituisce true se il salvataggio del progetto avviene in maniera corretta
     * @throws SQLException solleva questa eccezione quando c'e' un problema di connessione o problemi di esecuzione del comando SQL.
     */
    public boolean setProgetto(String email, String titolo, String path) throws SQLException {
        String query = "insert into progetti (email, titolo, path) values (?,?,?);";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,email);
        statement.setString(2,titolo);
        statement.setString(3,path);
        statement.executeUpdate();
        query = new String();
        statement.close();
        return true;
    }
    /**
     * il metodo getProgetti restituisce tutti i progetti presenti all'interno del database.
     * @return restituisce tutti i progetti del database
     */
    public ArrayList<Progects> getProgetti() throws SQLException {
        String query = "select email,titolo,path from progetti;";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        ArrayList<Progects> listProgetti= new ArrayList<Progects>();
        while(resultSet.next()){
            String email = resultSet.getString(1);
            String titolo = resultSet.getString(2);
            String path = resultSet.getString(3);
            Progects progetto = new Progects(titolo,email,path);
            listProgetti.add(progetto);
        }
        resultSet.close();
        statement.close();
        return listProgetti;
    }
    
}
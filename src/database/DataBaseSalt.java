package database;

import java.security.SecureRandom;
import java.sql.*;
import java.util.Random;


import com.google.errorprone.annotations.Immutable;

import exception.DatabaseConnectionException;
import exception.ErrorEmailException;

@Immutable
/**
 * La classe DataBaseSalt serve per connettermi al database dove contiene il salt della password.
 * @author giovanni
 *
 */
public final class DataBaseSalt {

    private String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private String DBMS = "jdbc:mysql";
    private String SERVER = "localhost";
    private String DATABASE = "salt";
    private String USER_ID = "SicurezzaSalt";
    private String PASSWORD = "salt";

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
     * il metodo setSalt inserisce un salt associato all'email.
     * @param email email che vogliamo inserire.
     * @return restituisce true se il comando sql è avvenuto con successo.
     * @throws SQLException solleva quando ci sono problemi ad eseguire il comando SQL.
     */
    public boolean setSalt(String email) throws SQLException{
        byte[] salt = createSalt();
        String query = "insert into utenti (email, salt) values (?,?);";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, email);
        statement.setBytes(2, salt);
        statement.executeUpdate();
        statement.close();
        return true;




    }
    /**
     * Il metodo getSalt restituisce il salt associato all'email.
     * @param email email da esaminare.
     * @return restituisce il salt associata all'email.
     * @throws ErrorEmailException restituisce questo errore se non esiste nessun salt associato a questa email.
     * @throws SQLException solleva quando ci sono problemi ad eseguire il comando SQL.
     */
    public byte[] getSalt(String email) throws ErrorEmailException, SQLException{
        Statement statement = conn.createStatement();
        try {
            String query = "select salt from utenti where email ='" + email +"';";
            ResultSet ris = statement.executeQuery(query);
            ris.next();
            byte[] salt = ris.getBytes("salt");
            statement.close();
            ris.close();
            return salt;
        } catch (SQLException e) {
        	throw new ErrorEmailException("Email sbagliata");
        }
    }
    /**
     * il metodo createSalt crea la stringa da 32 byte per creare il salt.
     * @return restituisce il salt creato.
     */
    private byte[] createSalt() {
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        return salt;
    }

}
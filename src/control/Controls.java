package control;

import java.io.*;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.google.errorprone.annotations.Immutable;


@Immutable
/**
 *
 * La classe controls controlla se i vari parametri passati in input sono corretti.
 */
public final class Controls {
	/**
	 * il metodo controlEmail controlla se il formato dell'email e' corretto
	 * @param email in input da controllare
	 * @return restituisco true se l'email e' corretta falso altrimenti.
	 */
    public static boolean controlEmail(String email) {
        String rule = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
        Pattern pattern = Pattern.compile(rule);
        return pattern.matcher(email).matches();
    }
    /**
     * il metodo controlTitolo controlla se il formato del titolo e' corretto
     * @param title in input da controllare
     * @return restituisco true se il titolo e' corretto falso altrimenti.
     */
    public static boolean controlTitolo(String title){
        String rule = "^[a-z-A-Z- ]{2,50}$";
        Pattern pattern = Pattern.compile(rule);
        return pattern.matcher(title).matches();
    }

    /**
     * il metodo controlDescription controlla se il contenuto del file non e' presente comandi sql.
     * @param description descrizione in input da controllare 
     * @return restituisco true se il file non contiene comandi sql falso altrimenti.
     */
    public static boolean controlDescription(String description){
        String rule = "^[a-z-A-Z-0-9- (),.'?\\\\\\[\\]-_.:,;!\\\"£$%&\\/|\\*èé°ç@ò#à§ùì+=*\\n\\r]{0,}$";
        Pattern pattern = Pattern.compile(rule);
        return pattern.matcher(description).matches();
    }
    /**
     * il metodo controlPassword controllo se il formato della password e' corretta
     * @param password da controllare
     * @return restituisco true se la password rispetta il formato indicato, falso altrimenti.
     * @throws UnsupportedEncodingException solleva se ci sono problemi di conversione.
     */
    public static boolean controlPassword(byte[]password) {
        String rule = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&]).{6,64}$";
        //Pattern pattern = Pattern.compile(rule);
        char[] passwordChar = new char[password.length];
        for (int i = 0; i<password.length;i++) {
        	passwordChar[i] = (char) password[i];
        }
        boolean ris = Pattern.matches(rule, CharBuffer.wrap(passwordChar));
        Arrays.fill(passwordChar,  '0');
        return ris;
    }
    /**
     * il metodo controlCookie controllo se il formato dell'id della sessione e' corretta,
     * @param id della sessione da controllare
     * @return restituisco true se il formato dell'id della sessione e' corretta, falso altrimenti.
     */
    public static boolean controlCookie(String id) {
    	String rule = "^[A-Za-z0-9]{20,20}$";
        Pattern pattern = Pattern.compile(rule);
        return pattern.matcher(id).matches();
    }

}

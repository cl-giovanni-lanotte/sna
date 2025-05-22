package Cookies;

import com.google.errorprone.annotations.Immutable;

@Immutable
/**
 * la classe CookiesContainer è un contenitore contenente id della sessione, la data di scadenza e l'email associata a quel cookie.
 * @author giovanni
 *
 */
public final class CookiesContainer {
    private String id;
    private String date;
    private String email;
    /**
     * Il costruttore che serve per inserire i valori dell'id, email e la data di scadenza.
     * @param id della sessione
     * @param email associata al cookie
     * @param date è la data di scadenza
     */
    public CookiesContainer(String id, String email, String date){
        this.id = id;
        this.date = date;
        this.email = email;
    }
    /**
     * il metodo getDate restituisce la data della sessione associata al cookie.
     * @return restituisce la data di scadenza del cookie.
     */
    public String getDate() {
        return date;
    }
    /**
     * il metodo getId restituisce l'id di sessione del cookie.
     * @return restituisce l'id di sessione del cookie.
     */
    public String getId() {
        return id;
    }
    /**
     * il metodo getEmail restituisce l'email associata al cookie.
     * @return restituisce l'email del coookie.
     */
    public String getEmail() {
        return email;
    }
}

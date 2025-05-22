package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.errorprone.annotations.Immutable;

import control.Download;

@Immutable
/**
 * la classe Projects e' una classe contenete il proprietario della richiesta del progetto, titolo e il file contenente la descrizione del progetto.
 * @author giovanni
 *
 */
public final class Progects {
    private String title;
    private String email;
    private String path;
    /**
     * il costruttore inserisco i parametri titolo, email e il nome del file.
     * @param titolo del progetto
     * @param email nome del proprietario
     * @param path nome del file.
     */
    public Progects(String titolo, String email, String path){
        this.title=titolo;
        this.email=email;
        this.path=path;
    }
    /**
     * il metodo getTitle restituisco il nome del titolo.
     * @return il titolo del progetto.
     */
    public String getTitle(){
        return title;
    }
    /**
     * il metodo getDescription restituisco il contenuto contenuto nel file.
     * @return restituisce il contenuto del file.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     */
    public String getDescription() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(Download.path+Download.DIR_TXT+path));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
        	stringBuilder.append(line);
        	stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    	
    }
    /**
     * il metodo getEmail restituisce il prorprietario dell'email
     * @return l'email del proprietario.
     */
    public String getEmail(){
        return email;
    }
    /**
     * il metodo getPath restituisce il path del file, della richiesta del progetto
     * @return path del file.
     */
    public String getPath() {
        return Download.DIR_TXT+path;
    }
}

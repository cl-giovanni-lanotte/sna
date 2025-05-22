package exception;

import com.google.errorprone.annotations.Immutable;

@Immutable
/**
 * la classe DatabaseConnectionException viene sollevata quando ci sono problemi di connessione al Database.
 * @author giovanni
 *
 */
public final class DatabaseConnectionException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseConnectionException(String string) {
        super(string);
    }

}

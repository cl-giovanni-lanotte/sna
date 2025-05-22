package exception;

import com.google.errorprone.annotations.Immutable;

@Immutable
/**
 * la classe PasswordException viene sollevata quando la password dell'utente non e' corretta.
 * @author giovanni
 *
 */
public final class PasswordException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordException(String string) {
        // TODO Auto-generated constructor stub
        super(string);
    }

}

package exception;

import com.google.errorprone.annotations.Immutable;

@Immutable
/**
 * la classe ErrorEmailException viene sollevata quando l'email non e' presente nel database.
 * @author giovanni
 *
 */
public final class ErrorEmailException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorEmailException(String string) {
        super(string);
    }

}

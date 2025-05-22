package exception;

import com.google.errorprone.annotations.Immutable;

@Immutable
/**
 * la classe ContentFileException viene sollevata quando il file contiene dei comandi html all'interno del file.
 * @author giovanni
 *
 */
public final class ContentFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

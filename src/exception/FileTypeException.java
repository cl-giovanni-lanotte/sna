package exception;

import com.google.errorprone.annotations.Immutable;

@Immutable
/**
 * la classe FileTypeException viene sollevata quando il file non e' di tipo TXT.
 * @author giovanni
 *
 */
public final class FileTypeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileTypeException(String string) {
        // TODO Auto-generated constructor stub
        super(string);
    }

}
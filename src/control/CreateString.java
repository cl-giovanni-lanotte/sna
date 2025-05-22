package control;

import com.google.errorprone.annotations.Immutable;

@Immutable
/*
 * Questa classe serve per creare una stringa di lunghezza indicata nella creazione della classe.
 */
public final class CreateString {
    private int lenght;
    public CreateString(int lenght){
    this.lenght=lenght;
    }
    public String createString(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(this.lenght);
        for (int i = 0; i < this.lenght; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}

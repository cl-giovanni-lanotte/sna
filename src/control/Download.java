package control;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.google.common.io.ByteStreams;
import com.google.errorprone.annotations.Immutable;

import exception.ContentFileException;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Immutable
/**
 * La classe serve per caricare il file, indicato nel percorso path.
 * @author giovanni
 *
 */
public final class Download {
	private static final String workspace = "/workspace-sna";
	
    public static final String path = System.getProperty("user.dir")+workspace+"/sna/WebContent/";
    public static final String DIR_IMAGE = "Images/";
    public static final String DIR_TXT = "Txt/";
    private Part part;
    /**
     * Il costruttore prende l'oggetto della classe Part da caricare all'interno del sistema.
     * @param part
     */
    public Download(Part part) {
    	this.part = part;
    }
    /**
     * il metodo isImage serve per controllare se l'oggetto della classe Part è un immagine.
     * @return restutuisce true nel caso se il file è un immagine, false altrimenti.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     * @throws TikaException solleva l'eccezione quando ci sono problemi ad controllare i metadati.
     * @throws SAXException solleva l'eccezione quando ci sono problemi nel calcolo del parse relativo al metadata.
     */
    public boolean isImagine() throws IOException, TikaException, SAXException {
        String nameFile = "temp."+typeFile();
        File file;
        file = uploadImage(nameFile);
        boolean ris = getMetadata(file).get("Content-Type").equals("image/jpeg");
        file.delete();
        return ris ;
    }
    /**
     * il metodo isTxt serve per controllare se l'oggetto della classe Part è un txt.
     * @return restutuisce true nel caso se il file è un TXT, false altrimenti.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     * @throws TikaException solleva l'eccezione quando ci sono problemi ad controllare i metadati.
     * @throws SAXException solleva l'eccezione quando ci sono problemi nel calcolo del parse relativo al metadata.
     * @throws ContentFileException solleva l'eccezione se il file contiene dei comandi html.
     */
    public boolean isTxt()throws IOException, TikaException, SAXException, ContentFileException{
        String nameFile = "temp."+typeFile();
        File file;
        file = uploadTxt(nameFile);
        boolean ris = getMetadata(file).get("Content-Type").contains("text/plain");
        if (ris){
        		String text =FileUtils.readFileToString(file, "utf-8");
        	  	if (!Controls.controlDescription(text)) {
	        	throw new ContentFileException();
	        }
        }
        file.delete();
        return ris;
    }
	/**
	 * il metodo getMetadata restituisce il metadata di un file.
	 * @param file il file da esaminare
	 * @return restitusce il metadata del file dato in input
	 * @throws TikaException solleva l'eccezione quando ci sono problemi ad controllare i metadati.
	 * @throws SAXException solleva l'eccezione quando ci sono problemi nel calcolo del parse relativo al metadata.
	 * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
	 */
    private Metadata getMetadata(File file) throws TikaException, SAXException, IOException {
    	AutoDetectParser parser = new AutoDetectParser(); 
    	BodyContentHandler handler = new BodyContentHandler(-1); 
    	Metadata metadata = new Metadata(); 
    	ParseContext context = new ParseContext(); 
        FileInputStream inputstream = new FileInputStream(file);
        parser.parse(inputstream, handler, metadata,context);
        return metadata;
    }
    /**
     * il metodo uploadImage serve per caricare il file nella cartella Image.
     * @return restituisce il file salvato.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     */
    public File uploadImage() throws IOException {
        File file;
        CreateString random = new CreateString(10);
        file = uploadImage(random.createString()+".jpg");
        return file;
    }
    /**
     * il metodo uploadImage prende in input il nome dell'immagine e lo salva all'interno della cartella Image.
     * @param nameFile nome del file da salvare
     * @return restituisce l'oggetto File, che è stato salvato.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     */
    private File uploadImage(String nameFile) throws IOException {
        InputStream is = part.getInputStream();
        File dir = new File(path+ Download.DIR_IMAGE);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(path+Download.DIR_IMAGE+nameFile);
        file.createNewFile();
        FileOutputStream os = new FileOutputStream(file);
        byte[] ch = ByteStreams.toByteArray(is);
        os.write(ch);
        is.close();
        os.close();
        return file;
    }
    /**
     * il metodo uploadImage serve per caricare il file nella cartella Txt.
     * @return restituisce il file salvato.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     */
    public File uploadTxt() throws IOException{
        CreateString random = new CreateString(10);
        File file;
        file = uploadTxt(random.createString()+".txt");
        
        return file;
    }
    /**
     * Il metodo uploadTxt prende in input il nome dell'immagine e lo salva all'interno della cartella Txt.
     * @param nameFile nome del file da salvare
     * @return restituisce l'oggetto File, che è stato salvato.
     * @throws IOException solleva l'eccezione quando ci sono problemi di creazione\lettura\scrittura del file.
     */
    private File uploadTxt(String nameFile) throws IOException {
        InputStream is = part.getInputStream();
        File dir = new File(path + Download.DIR_TXT);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(path+ Download.DIR_TXT +nameFile);
        String ris = IOUtils.toString(is,StandardCharsets.UTF_8);
        file.createNewFile();
        FileOutputStream os = new FileOutputStream(file);
        os.write(ris.getBytes());
        is.close();
        os.close();
        return file;
        
    }
    /**
     * Il metodo typeFile controlla se il file è un immagine o un txt, restituendo il tipo di file txt o jpg.
     * @return restituisce il tipo di file.
     */
    private String typeFile() {
        if(part.getContentType().contains("jpeg"))
            return "jpg";
        else
            return "txt";
    }
}

package de.eldecker.dhbw.spring.bildergallerie.logik.exceptions;


/**
 * Eigene Exception-Klasse für Fehler, dass MIME-Type von hochgeladenem Bild
 * nicht bestimmt werden kann oder nicht unterstützt wird.
 */
@SuppressWarnings("serial")
public class MimeTypeException extends Exception {
    
    /**
     * Konstruktor für Erzeugung Exception mit Fehlerbeschreibung.
     * 
     * @param nachricht Fehlerbeschreibung
     */
    public MimeTypeException( String nachricht ) {
        
        super ( nachricht );
    }
 
    
    /**
     * Konstruktor für Erzeugung Exception mit Fehlerbeschreibung
     * und auslösender Exception (z.B. {@code IOException}).
     * 
     * @param nachricht Fehlerbeschreibung
     * 
     * @param ex auslösende Exception
     */    
    public MimeTypeException( String nachricht, Exception ex ) {
        
        super( nachricht, ex );
    }
    
}

package de.eldecker.dhbw.spring.bildergallerie.logik.exceptions;


/**
 * Eigene Exception-Klasse für Fehler, dass MIME-Type von hochgeladenem Bild
 * nicht bestimmt werden kann.
 */
@SuppressWarnings("serial")
public class MimeTypeException extends Exception {

    public MimeTypeException( String bildTitel ) {
        
        super( bildTitel );
    }
    
}

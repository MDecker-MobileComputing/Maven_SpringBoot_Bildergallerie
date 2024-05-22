package de.eldecker.dhbw.spring.bildergallerie.logik.exceptions;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;


/**
 * Eigene Exception-Klasse für den Fall, dass versucht wird, ein Bild hochzuladen,
 * das laut Hash-Wert schon in der Datenbank vorhanden ist. Das Exception-Objekt
 * kapselt das bereits vorhandene Bild als {@link BildEntity}.
 */
@SuppressWarnings("serial")
public class BildSchonVorhandenException extends Exception {

    /** Bild mit selben Hash-Wert, das schon in der DB gespeichert ist. */
    private final BildEntity _bildEntity;
    
    
    /**
     * Konstruktor. 
     * 
     * @param bildEntity Bereits in DB vorhandenes Bild mit demselben
     *                   Hash-Wert
     */
    public BildSchonVorhandenException( BildEntity bildEntity ) {
       
        super( "Bild mit selben Hash-Wert schon in DB gespeichert" );
        
        _bildEntity = bildEntity;
    }
    
    
    /**
     * Konstruktor mit eigener Exception-Nachricht {@code message}.
     * 
     * @param message Exception-Nachricht
     * 
     * @param bildEntity Bereits in DB vorhandenes Bild mit demselben
     *                   Hash-Wert
     */
    public BildSchonVorhandenException( String message, BildEntity bildEntity ) {
       
        super( message );
        
        _bildEntity = bildEntity;
    }    
    
    
    /**
     * Getter für das bereits vorhandene Bild. 
     * 
     * @return Bild mit selbem Hash-Wert, das schon in DB gespeichert
     *         ist
     */
    public BildEntity getBildEntity() {
        
        return _bildEntity;
    }
    
}

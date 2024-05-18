package de.eldecker.dhbw.spring.bildergallerie.logik;

import de.eldecker.dhbw.spring.bildergallerie.db.BildEntity;


/**
 * Eigene Exception-Klasse für den Fall, dass versucht wird, ein Bild hochzuladen,
 * das laut Hash-Wert schon in der Datenbank vorhanden ist.
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
     * Getter für das bereits vorhandene Bild. 
     * 
     * @return Bild mit selbem Hash-Wert, das schon in DB gespeichert
     *         ist
     */
    public BildEntity getBildEntity() {
        
        return _bildEntity;
    }
}
package de.eldecker.dhbw.spring.bildergallerie.logik.exceptions;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;


/**
 * Für Rechnerübung: Ein Bild mit diesem Titel ist schon in der
 * Datenbank vorhanden.
 */
@SuppressWarnings("serial")
public class TitelSchonVorhandenException extends BildSchonVorhandenException {

    public TitelSchonVorhandenException( BildEntity bild ) {
        
        super( "Bild mit Name/Titel schon vorhanden", bild );
    }
    
}

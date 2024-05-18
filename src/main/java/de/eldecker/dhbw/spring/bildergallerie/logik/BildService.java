package de.eldecker.dhbw.spring.bildergallerie.logik;

import java.sql.Blob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.bildergallerie.db.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;


/**
 * Diese Klasse enthält die Methoden mit der Geschäftslogik für 
 * die Arbeit mit Bildern.
 */
@Service
public class BildService {

    private final static Logger LOG = LoggerFactory.getLogger( BildService.class );
    
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;
            
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public BildService( BildRepository bildRepo ) {
        
        _bildRepo = bildRepo;
    }
    
    
    /**
     * Von Nutzer über Webseite hochgeladenes Bild in Datenbank speichern. 
     * 
     * 
     * @param titel Titel des Bildes (vom Nutzer eingegeben), sollte schon
     *              getrimmt sein
     * 
     * @param byteArray Byte-Array mit Bilddaten (Binärdaten)
     * 
     * @return ID des neuen Datensatzes 
     */
    public long bildHochladen( String titel, byte[] byteArray ) {
                        
        final Blob blob = BlobProxy.generateProxy( byteArray );
        
        final BildEntity bild = new BildEntity( titel , blob );
        
        final BildEntity ergebnisEntity = _bildRepo.save( bild );
        
        return ergebnisEntity.getId();
    }
    
}

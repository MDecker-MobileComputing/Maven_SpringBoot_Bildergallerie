package de.eldecker.dhbw.spring.bildergallerie.logik;

import java.sql.Blob;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.bildergallerie.db.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.helferlein.MD5Hasher;


/**
 * Diese Klasse enthält die Methoden mit der Geschäftslogik für 
 * die Arbeit mit Bildern.
 */
@Service
public class BildService {

    private final static Logger LOG = LoggerFactory.getLogger( BildService.class );
    
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;
    
    /** Hilfs-Bean für MD5-Berechnung. */
    private final MD5Hasher _md5hasher;
            
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public BildService( BildRepository bildRepo,
                        MD5Hasher md5hasher) {
        
        _bildRepo  = bildRepo;
        _md5hasher = md5hasher;
    }
    
    
    /**
     * Von Nutzer über Webseite hochgeladenes Bild in Datenbank speichern.
     * 
     * @param titel Titel des Bildes (vom Nutzer eingegeben), sollte schon
     *              getrimmt sein
     * 
     * @param byteArray Byte-Array mit Bilddaten (Binärdaten)
     * 
     * @return ID des neuen Datensatzes 
     * 
     * @throws BildSchonVorhandenException Bild mit selbem Hash-Wert ist schon in DB vorhanden
     */
    public long bildHochladen( String titel, byte[] byteArray ) throws BildSchonVorhandenException {
        
        final String md5hash = _md5hasher.getHash( byteArray );
        
        final Optional<BildEntity> altesBildOptional = _bildRepo.findByHash( md5hash );
        
        if ( altesBildOptional.isPresent() ) {
            
            final BildEntity altesBild = altesBildOptional.get(); 
            throw new BildSchonVorhandenException( altesBild );
        }
        
        
        final Blob blob = BlobProxy.generateProxy( byteArray );
                        
        final BildEntity bild = new BildEntity( titel , blob, md5hash );
        
        final BildEntity ergebnisEntity = _bildRepo.save( bild );
        
        return ergebnisEntity.getId();
    }
     
    
}

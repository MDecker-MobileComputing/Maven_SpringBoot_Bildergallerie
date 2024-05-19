package de.eldecker.dhbw.spring.bildergallerie.logik;

import static java.util.Optional.empty;

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
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.BildSchonVorhandenException;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.MimeTypeException;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

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
                        MD5Hasher md5hasher ) {
        
        _bildRepo  = bildRepo;
        _md5hasher = md5hasher;
    }
    
    
    /**
     * Von Nutzer über Webseite hochgeladenes Bild in Datenbank speichern. Es wird zuerst
     * überprüft, ob schon ein Bild mit demselben Hash-Wert in der Datenbank gespeichert
     * ist.
     * 
     * @param titel Titel des Bildes (vom Nutzer eingegeben), sollte schon getrimmt sein              
     * 
     * @param byteArray Byte-Array mit Bilddaten (Binärdaten)
     * 
     * @return ID des neuen Datensatzes 
     * 
     * @throws BildSchonVorhandenException Bild mit selbem Hash-Wert ist schon in DB vorhanden
     */
    public long bildHochladen( String titel, byte[] byteArray ) throws BildSchonVorhandenException, MimeTypeException {
        
        final String md5hash = _md5hasher.getHash( byteArray );
        
        final Optional<BildEntity> altesBildOptional = _bildRepo.findByHash( md5hash );
        
        if ( altesBildOptional.isPresent() ) {
            
            final BildEntity altesBild = altesBildOptional.get(); 
            throw new BildSchonVorhandenException( altesBild );
        }
        
        
        final String mimeTypeOptional = mimeTypeBestimmen( byteArray, titel ); // throws MimeTypeException
        
        LOG.info( "MIME-Type von Bild: " + mimeTypeOptional );
        
        final Blob blob = BlobProxy.generateProxy( byteArray );
                        
        final BildEntity bild = new BildEntity( titel , blob, md5hash );
        
        final BildEntity ergebnisEntity = _bildRepo.save( bild );
        
        return ergebnisEntity.getId();
    }
    
    
    /**
     * MIME-Typ von hochgeladenem Bild bestimmen. Intern wird die Bibliothek "jMimeMagic"
     * verwendet.
     * 
     * @param byteArray Byte-Array mit Binärdaten des Bildes 
     * 
     * @param titel Titel des Bilds wird für Exception benötigt
     * 
     * @return Mime-Typ des Bilds. Beispiele: "image/jpeg", "image/png", "image/gif"
     * 
     * @throws MimeTypeException Mime-Typ konnt nicht bestimmt werden
     */
    private String mimeTypeBestimmen( byte[] byteArray, String titel ) throws MimeTypeException { 
                                                          
        try {
        
            final MagicMatch match = Magic.getMagicMatch( byteArray, false );
            
            final String mimeString = match.getMimeType();
            
            LOG.info( "Dateityp-Beschreibung: {}", match.getDescription() );
            
            return mimeString;
                
        } catch ( MagicParseException | MagicMatchNotFoundException | MagicException ex ) {
            
            LOG.error( "MIME-Type von Grafikdatei konnte nicht bestimmt werden.", ex );            
            
            throw new MimeTypeException( titel );
        }                
    }     
    
}

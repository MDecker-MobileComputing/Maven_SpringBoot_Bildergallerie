package de.eldecker.dhbw.spring.bildergallerie.logik;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.Optional;

import org.apache.tika.Tika;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.bildergallerie.db.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.helferlein.MD5Hasher;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.BildSchonVorhandenException;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.MimeTypeException;


/**
 * Diese Klasse enthält die Methoden mit der Geschäftslogik für 
 * die Arbeit mit Bildern.
 */
@Service
public class BildService {
    
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;
    
    /** Hilfs-Bean für MD5-Berechnung. */
    private final MD5Hasher _md5hasher;
    
    /** Objekt für Bestimmung MIME-Type von Grafikdatei (Apache Tika). */
    private final Tika _tika = new Tika();
    
    
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
     * @return Neu erzeugtes Bild
     * 
     * @throws BildSchonVorhandenException Bild mit selbem Hash-Wert ist schon in DB vorhanden
     */
    public BildEntity bildHochladen( String titel, byte[] byteArray ) 
                                     throws BildSchonVorhandenException, MimeTypeException {
        
        final String md5hash = _md5hasher.getHash( byteArray );
        
        final Optional<BildEntity> altesBildOptional = _bildRepo.findByHash( md5hash );
        
        if ( altesBildOptional.isPresent() ) {
            
            final BildEntity altesBild = altesBildOptional.get(); 
            throw new BildSchonVorhandenException( altesBild );
        }
                
        final String mimeTyp = mimeTypeBestimmen( byteArray, titel ); // throws MimeTypeException        
        
        final Blob blob = BlobProxy.generateProxy( byteArray );
                        
        final BildEntity bild = new BildEntity( titel , blob, md5hash, mimeTyp );
        
        final BildEntity ergebnisEntity = _bildRepo.save( bild ); // eigentliches Speichern in DB
        
        return ergebnisEntity;
    }
    
    
    /**
     * MIME-Typ von hochgeladenem Bild bestimmen. Intern wird die Bibliothek "Apache Tika"
     * verwendet.
     * 
     * @param byteArray Byte-Array mit Binärdaten des Bildes 
     * 
     * @param titel Titel des Bilds wird für Exception benötigt
     * 
     * @return MIME-Typ des Bilds. 
     *         Unterstützte Werte: "image/jpeg", "image/png", "image/gif", "image/svg+xml"
     * 
     * @throws MimeTypeException MIME-Typ konnte nicht bestimmt werden oder ist keiner
     *                           der unterstützten Typen (siehe Beschreibung möglicher
     *                           {@code return}-Werte). 
     */
    private String mimeTypeBestimmen( byte[] byteArray, String titel ) throws MimeTypeException { 
                                                          
        try {

            final ByteArrayInputStream inputStream = new ByteArrayInputStream( byteArray );
            
            final String mimeType = _tika.detect( inputStream ); // throws IOException            
            switch ( mimeType ) {
            
                case "image/jpeg"   :
                case "image/png"    :
                case "image/gif"    :
                case "image/svg+xml": return mimeType;
                    
                default: throw new MimeTypeException( "Nicht unterstützter MIME-Type \"" + mimeType + "\"" );
            }                        
        }
        catch ( IOException ex ) {
            
            throw new MimeTypeException( 
                           "Ein-/Ausgabe-Fehler bei Bestimmung MIME-Type von Bild mit Titel \"" + titel + "\"", 
                           ex );
        }        
    }     
    
}

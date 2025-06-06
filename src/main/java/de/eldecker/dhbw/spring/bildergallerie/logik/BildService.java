package de.eldecker.dhbw.spring.bildergallerie.logik;

import static java.util.Collections.emptyList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import org.apache.tika.Tika;
import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.TagRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;
import de.eldecker.dhbw.spring.bildergallerie.helferlein.MD5Hasher;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.BildSchonVorhandenException;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.MimeTypeException;


/**
 * Diese Klasse enthält die Methoden mit der Geschäftslogik für 
 * die Arbeit mit Bildern.
 */
@Service
public class BildService {
    
    private final static Logger LOG = LoggerFactory.getLogger( BildService.class );
    
    
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;
    
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Tags. */
    private final TagRepository _tagRepo;
    
    /** Hilfs-Bean für MD5-Berechnung. */
    private final MD5Hasher _md5hasher;
    
    /** Objekt für Bestimmung MIME-Type von Grafikdatei (Apache Tika). */
    private final Tika _tika = new Tika();
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public BildService( BildRepository bildRepo,
                        MD5Hasher md5hasher,
                        TagRepository tagRepo) {
        
        _bildRepo  = bildRepo;
        _md5hasher = md5hasher;
        _tagRepo   = tagRepo;
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
     * @param tagListe Tags, die dem Bild zugeordnet werden sollen; unbekannte
     *                 Tags werden einfach ignoriert; darf leer sein
     * 
     * @return Neu erzeugtes Bild
     * 
     * @throws BildSchonVorhandenException Bild mit selbem Hash-Wert ist schon in DB vorhanden
     */
    public BildEntity bildHochladen( String titel, byte[] byteArray, List<String> tagListe ) 
                      throws BildSchonVorhandenException, MimeTypeException {
        
        final String md5hash = _md5hasher.getHash( byteArray );
        
        final Optional<BildEntity> bildByHash = _bildRepo.findByHash( md5hash );        
        if ( bildByHash.isPresent() ) {
            
            final BildEntity altesBild = bildByHash.get(); 
            throw new BildSchonVorhandenException( altesBild );
        }
            

        final String mimeTyp = mimeTypeBestimmen( byteArray, titel ); // throws MimeTypeException        
        
        final Blob blob = BlobProxy.generateProxy( byteArray );
                        
        final BildEntity bild = new BildEntity( titel , blob, md5hash, mimeTyp );
        
        final BildEntity savedEntity = _bildRepo.save( bild ); // eigentliches Speichern in DB
        
        final BildEntity ergebnisEntity = tagsHinzufuegen( savedEntity, tagListe );
        
        return ergebnisEntity;                
    }
    
    
    /**
     * Tags mit Namen aus {@code tagListe} der {@code bildEntity} zuzuordnen
     * 
     * @param bildEntity Bild, dem Tags zugeordnet werden sollen.
     * 
     * @param tagListe Liste der Namen von Tags, die {@code buildEntity} zugeordnet werden sollen;
     *                 unbekannte Tags werden ignoriert (es wird aber eine Warnung ins Log
     *                 geschrieben)
     * 
     * @return Bild nach hinzufügen von Tags
     */
    public BildEntity tagsHinzufuegen( BildEntity bildEntity, List<String> tagListe ) {
    	
    	if ( tagListe == null || tagListe.isEmpty() ) {
    		
    		return bildEntity;
    	}
    	
    	int tagsZugeordnetZaehler = 0;
    	
    	for ( String tagName : tagListe ) {
    		
    		final Optional<TagEntity> tagEntityOptional = _tagRepo.findByName( tagName );
    		if ( tagEntityOptional.isEmpty() ) {
    			
    			LOG.warn( "Versuch Tag \"{}\" dem Bild mit ID={} zuzuordnen, aber kein Tag mit diesem Namen gefunden.", 
    					  tagName, bildEntity.getId() );    			
    		} else {
    			
    			final TagEntity tag = tagEntityOptional.get();
    			bildEntity.addTag( tag );
    			tagsZugeordnetZaehler++;
    		}
    	}
    	
    	LOG.info( "Anzahl Tags zu Bild mit ID={} zugeordnet: {}", bildEntity.getId(), tagsZugeordnetZaehler );
    	
    	return _bildRepo.save( bildEntity );
    }
    
    
    /**
     * Convience-Methode: Überladung von {@link #bildHochladen(String, byte[], List)}
     * mit leerer Tag-Liste.
     */
    public BildEntity bildHochladen( String titel, byte[] byteArray ) 
                      throws BildSchonVorhandenException, MimeTypeException {
    	
    	return bildHochladen( titel, byteArray, emptyList() );
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
            
                case "image/jpeg"    :
                case "image/png"     :
                case "image/gif"     :
                case "image/svg+xml" : return mimeType;
                    
                default: 
                    throw new MimeTypeException( "Nicht unterstützter MIME-Type \"" + mimeType + "\"." );
            }                        
        }
        catch ( IOException ex ) {
            
            throw new MimeTypeException( 
                           "Ein-/Ausgabe-Fehler bei Bestimmung MIME-Type von Bild mit Titel \"" + titel + "\"", 
                           ex );
        }        
    }     
    
    
    /**
     * Liefert sortierte Liste aller Bilder zurück.
     * 
     * @param sortierAttribut Attribut, nach dem die Liste aufsteigend sortiert werden soll.
     * 
     * @return Iterator mit allen Bildern in der Datenbank, sortiert nach {@code sortierAttribut}
     */
    public List<BildEntity> getBildListe( SortierAttributEnum sortierAttribut ) {
        
        final String techNameAttribut = sortierAttribut.getEntityAttributName();
        
        final Sort sort = Sort.by( techNameAttribut );
        
        return _bildRepo.findAll( sort );
    }
    
    
    /**
     * Ein Tag einem Bild hinzufügen. Es wird keine Exception geworfen, wenn {@code tag}
     * schon {@code bild} zugeweisen war.
     * 
     * @param bild Bild, das einen neuen Tag bekommen soll.
     * 
     * @param tags Ein oder mehrere Tags, die dem Bild hinzugefügt werden sollen
     * 
     * @return Bild nach Hinzufügen von {@code tag}
     */
    public BildEntity tagsHinzufuegen( BildEntity bild, TagEntity... tags ) {
        
        for ( TagEntity tag : tags ) {
            
            final boolean tagDazu = bild.addTag( tag );            
            if ( tagDazu == false ) {
                
                LOG.warn( "Tag \"{}\" war schon Bild mit ID={} zugewiesen.", 
                          tag.getName(), bild.getId() );
            }
        }
        
        return _bildRepo.save(bild);
    }
    
}

package de.eldecker.dhbw.spring.bildergallerie.logik;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.TagRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.BildSchonVorhandenException;
import de.eldecker.dhbw.spring.bildergallerie.logik.exceptions.MimeTypeException;



/**
 * Wenn die Datenbank leer ist, dann importiert diese Bean einige Beispielbilder
 * in die Datenbank. Da sie das Interface {@code ApplicationRunner} implementiert
 * wird die darin enthaltene Methode automatisch beim Start der Anwendung
 * ausgeführt.  
 */
@Component
public class BeispielDatenImporter implements ApplicationRunner {
    
    private final static Logger LOG = LoggerFactory.getLogger( BeispielDatenImporter.class );
        
    /** Repository-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;
    
    /** Repository-Bean für Zugriff auf Datenbanktabelle mit Tags. */
    private final TagRepository _tagRepo;
    
    /** Service-Bean mit Geschäftslogik für Bildern, wird hier zum Speichern von Bildern benötigt. */
    private final BildService _bildService;
        
    /** Bean zum Laden der Beispielbilder. */
    private final ResourceLoader _resourceLoader;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public BeispielDatenImporter( BildRepository bildRepo,
                                  TagRepository tagRepo,
                                  ResourceLoader resourceLoader,
                                  BildService bildService ) {
        
        _bildRepo       = bildRepo;
        _tagRepo        = tagRepo;
        _resourceLoader = resourceLoader;
        _bildService    = bildService;
    }
    
    
    /**
     * Implementierung der einzige abstrakten Methode aus dem
     * Interface {@code ApplicationRunner}, wird beim Start der
     * Anwendung automatisch ausgeführt.
     * 
     * @param args CmdLine-Argumente, werden nicht ausgewertet 
     */
    @Override
    public void run( ApplicationArguments args ) throws Exception {
                                
        final long anzahlBilderVorher = _bildRepo.count();  
        if ( anzahlBilderVorher > 0 ) {
            
            LOG.info( "Die Datenbank enthält schon {} Bilder, deshalb werden keine Demo-Daten importiert.", 
                      anzahlBilderVorher );            
        } else {
            
            LOG.info( "Die Datenbank enthält keine Bilder, versuche deshalb, Demo-Daten zu importieren." );
            
            // Tags definieren (Einzahl)
            final TagEntity tagKatze     = tagAnlegen( "Katze"     );
            final TagEntity tagHund      = tagAnlegen( "Hund"      );
            final TagEntity tagTier      = tagAnlegen( "Tier"      );
            final TagEntity tagZeichnung = tagAnlegen( "Zeichnung" );
            
            // Tags ohne gleich Bilder zuzuordnen
            tagAnlegen( "Vogel"    );
            tagAnlegen( "Nagetier" );
            
            final BildEntity bild1 = ladeDemoBild( "Hund und Katze"      , "dog-5883275_1280.jpg"     );
            final BildEntity bild2 = ladeDemoBild( "Russische Nacktkatze", "mammals-3210053_1280.jpg" );
            final BildEntity bild3 = ladeDemoBild( "Katze (Zeichnung)"   , "cute-7270285_1280.png"    );
            final BildEntity bild4 = ladeDemoBild( "Rakete um Erde"      , "rocket-3972.gif"          );
            final BildEntity bild5 = ladeDemoBild( "Hund (Labrador)"     , "dog-8198719_1280.jpg"     );
                                    
            final long anzahlBilderNachher = _bildRepo.count();
            final long anzahlTags          = _tagRepo.count();
            LOG.info( "Demo-Bilder geladen, DB enthält jetzt {} Bilder und {} Tags.", 
                      anzahlBilderNachher, anzahlTags );
            
            // den Bilden noch Tags hinzufügen
            _bildService.tagsHinzufuegen( bild1, tagKatze, tagHund, tagTier      ); 
            _bildService.tagsHinzufuegen( bild2, tagKatze, tagTier               );
            _bildService.tagsHinzufuegen( bild3, tagKatze, tagTier, tagZeichnung );
            _bildService.tagsHinzufuegen( bild4, tagZeichnung                    );
            _bildService.tagsHinzufuegen( bild5, tagHund, tagTier                );
        }
    }
        
    
    /**
     * Bild aus Ressourcenordner {@code demo-bilder} in DB laden.
     * 
     * @param titel Name des Bildes, z.B. "Hund und Katze"
     * 
     * @param dateiname Dateiname im Ordner {@code demo-bilder}
     * 
     * @return Erzeugtes Bild; ist {@code null} wenn Bild nicht erzeugt werden konnte.
     */
    private BildEntity ladeDemoBild( String titel, String dateiname ) {
        
        try {
            
            final byte[] byteArray = ladeBildRessource( dateiname ); // throws IOException
            
            final BildEntity bild = _bildService.bildHochladen( titel, byteArray ); // throws BildSchonVorhandenException
            
            LOG.info( "Demo-Bild \"{}\" unter ID={} abgespeichert.", dateiname, bild.getId() );   
            
            return bild;
        }                
        catch ( IOException | BildSchonVorhandenException | MimeTypeException ex ) {
            
            LOG.error( "Fehler beim Laden von Bilddatei \"{}\".", dateiname, ex );
            return null;
        }
    }
         
    
    /**
     * Hilfsmethode um ein Demo-Bild aus dem Ressource-Ordner {@code src/main/resource/demo-bilder}
     * zu laden. Alle Bilder in diesem Ordner sind freie Bilder von Pixabay. 
     * 
     * @param dateiname Dateiname, z.B. {@code dog-5883275_1280.jpg}.
     * 
     * @return Byte-Array mit Binärdaten von Bild
     * 
     * @throws IOException Ein-/Ausgabefehler
     */
    private byte[] ladeBildRessource( String dateiname ) throws IOException {
        
        final String resourceLocation = "classpath:demo-bilder/" + dateiname;
        
        final Resource resource = _resourceLoader.getResource( resourceLocation );
        
        return resource.getContentAsByteArray();
    }
    
    
    /**
     * Neuen Tag auf Datenbank anlegen.
     * 
     * @param name Anzeigename (ist auch gleichzeitig Technischer Name) des Tags
     * 
     * @return Neuer Tag
     */
    private TagEntity tagAnlegen( String name ) {
     
        final TagEntity tag = new TagEntity( name );

        return _tagRepo.save(tag );        
    }
        
}

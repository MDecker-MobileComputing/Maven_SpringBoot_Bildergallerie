package de.eldecker.dhbw.spring.bildergallerie.logik;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;



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
    
    /** Service-Bean mit Geschäftslogik für Bildern, wird hier zum Speichern von Bildern benötigt. */
    private final BildService _bildService;
    
    /** Bean zum Laden der Beispielbilder. */
    private final ResourceLoader _resourceLoader;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public BeispielDatenImporter( BildRepository bildRepo,
                                  ResourceLoader resourceLoader,
                                  BildService bildService ) {
        
        _bildRepo       = bildRepo;
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
            
            ladeDemoBild( "Hund und Katze"      , "dog-5883275_1280.jpg"     );
            ladeDemoBild( "Russische Nacktkatze", "mammals-3210053_1280.jpg" );
            
            final long anzahlBilderNachher = _bildRepo.count();
            LOG.info( "Demo-Bilder geladen, DB enthält jetzt {} Bilder.", anzahlBilderNachher );
        }
    }
    
    
    /**
     * Bild aus Ressourcenordner in DB laden.
     * 
     * @param titel Name des Bildes, z.B. "Hund und Katze"
     * 
     * @param dateiname Dateiname im Ordner {@code demo-bilder}
     */
    private void ladeDemoBild( String titel, String dateiname ) {
        
        try {
            
            final byte[] byteArray = ladeBildRessource( dateiname ); // throws IOException
            
            final long id = _bildService.bildHochladen( titel, byteArray );
            
            LOG.info( "Demo-Bild \"{}\" unter ID={} abgespeichert.", dateiname, id );            
        }                
        catch ( IOException ex ) {
            
            LOG.error( "Fehler beim Laden von Bilddatei \"{}\"", dateiname, ex );
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
     * @throws Exception Ein-/Ausgabefehler
     */
    private byte[] ladeBildRessource( String dateiname ) throws IOException {
        
        final String resourceLocation = "classpath:demo-bilder/" + dateiname;
        
        final Resource resource = _resourceLoader.getResource( resourceLocation );
        
        return resource.getContentAsByteArray();
    }
    
    
}

package de.eldecker.dhbw.spring.bildergallerie.web;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.eldecker.dhbw.spring.bildergallerie.db.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.logik.BildSchonVorhandenException;
import de.eldecker.dhbw.spring.bildergallerie.logik.BildService;

 

/**
 * Klasse stellt Endpunkt für Upload des Bilds bereit und liefert die entsprechende
 * Antwortseite für den Erfolgs- und Fehlerfall zurück.
 */
@Controller
@RequestMapping( "/app/" )
public class DateiUploadController {

    private static final Logger LOG = LoggerFactory.getLogger( DateiUploadController.class );
    
    /** Service-Bean mit Geschäftslogik */
    private BildService _bildService;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public DateiUploadController( BildService bildService ) {
    
        _bildService = bildService;
    }
    
    
    /**
     * Nimmt hochgeladenes Bild entgegen.
     * 
     * @param bild Hochgeladenes Bild
     * 
     * @param titel Vom Nutzer eingegebener Name für das Bild
     * 
     * @param attributeWeiterleitung Platzhalterwerte für das Template der 
     *                               Weiterleitungsseite
     * 
     * @return Weiterleitung auf Folgeseite für Erfolg oder Fehler; 
     *         die Weiterleitungs-Seiten sind alle auch in diesem Controller
     *         definiert
     */
    @PostMapping("/bild")
    public String bildHochladen( @RequestParam("bild")  MultipartFile bild,            
                                 @RequestParam("titel") String        titel,
                                 RedirectAttributes attributeWeiterleitung ) {
        
        if ( bild.isEmpty() ) {
            
            LOG.warn( "Upload mit leerem Bild." );
            return "redirect:upload-kein-bild";
        }
                                    
        final long   bytes       = bild.getSize();
        final int    kByte       = (int) ( bytes / 1024 );
        final String dateiName   = bild.getOriginalFilename();
        final String contentType = bild.getContentType();
        String titelNormal = titel.trim();

        LOG.info( "Bild hochgeladen: DateiName=\"{}\", ContentType=\"{}\", {} kBytes, Titel=\"{}\"", 
                  dateiName, contentType, kByte, titelNormal ); 
        
        if ( titelNormal.isBlank() ) {
            
            titelNormal = dateiName;
        }
        
        try {
        
            final byte[] byteArray = bild.getBytes(); // throws IOException
            
            try {
            
                final long id = _bildService.bildHochladen( titelNormal, byteArray ); // throws BildSchonVorhandenException
                
                LOG.info( "Bild mit Titel \"{}\" unter ID {} in DB gespeichert.", dateiName, id );
                
                attributeWeiterleitung.addFlashAttribute( "dateigroesse_kb", kByte );

                return "redirect:upload-erfolg";                
            }
            catch ( BildSchonVorhandenException ex ) {

                return behandleFehlerBildSchonVorhanden( ex, attributeWeiterleitung );
            }                                                
        }
        catch ( IOException ex ) {
            
            LOG.error( "I/O-Fehler bei Zugriff auf hochgeladene Bilddaten.", ex );
            return "redirect:upload-fehler";
        }                       
    }    
    
    
    /**
     * Behandlung für den Fall, dass das gerade hochgeladene Bild laut Hash-Wert schon in 
     * der Datenbank enthalten ist.
     * 
     * @param ex Exception-Objekt, enthält auch altes Bild
     * 
     * @param attributeWeiterleitung Platzhalterwerte für das Template der 
     *                               Weiterleitungs-Seite
     * 
     * @return Weiterleitungs-Seite für diesen Fehlerfall
     */
    private String behandleFehlerBildSchonVorhanden( BildSchonVorhandenException ex,
                                                     RedirectAttributes attributeWeiterleitung ) {
        
        final BildEntity altesBild = ex.getBildEntity();
        
        LOG.warn( "Versuch ein Bild hochzuladen, aber es gibt dieses Bild schon in der DB unter der ID={}.", 
                  altesBild.getId() );
        
        final String        altesBildTitel     = altesBild.getTitel();
        final LocalDateTime altesBildDatumZeit = altesBild.getZeitpunktErzeugung(); 
                        
        attributeWeiterleitung.addFlashAttribute( "altes_bild_titel"    , altesBildTitel     );
        attributeWeiterleitung.addFlashAttribute( "altes_bild_datumzeit", altesBildDatumZeit );
        
        try {
        
            final long altesBildLaenge = altesBild.getBild().length(); 
            final int kBytes = (int) ( altesBildLaenge / 1024);
            
            attributeWeiterleitung.addFlashAttribute( "altes_bild_kBytes", kBytes );            
        }
        catch ( SQLException ex2 ) {
         
            LOG.error( "Konnte nicht Länge von bereits in DB vorhandenem Bild (ID={}) auslesen.", 
                        altesBild.getId(), ex2 );
            
            attributeWeiterleitung.addFlashAttribute( "altes_bild_kBytes", -1 );
        }        
        
        return "redirect:upload-fehler-bild-schon-vorhanden";
    }

    
    /**
     * Weiterleitungs-Seite für erfolgreichen Upload anzeigen.
     * 
     * @return Name von Template-Datei
     */
    @GetMapping("/upload-erfolg")
    public String erfolg() {
        
        return "upload-erfolg";
    }

    
    /**
     * Weiterleitungs-Seite für Versuch ein "leeres" Bild hochzuladen anzeigen.
     * 
     * @return Name von Template-Datei
     */    
    @GetMapping("/upload-kein-bild")
    public String keinBild() {
        
        return "upload-fehler-kein-bild";
    }
    
    
    /**
     * Weiterleitungs-Seite wenn Bild schon in DB vorhanden war.
     * 
     * @return Name von Template-Datei
     */
    @GetMapping("/upload-fehler-bild-schon-vorhanden")
    public String bildSchonVorhanden() {
        
        return "upload-fehler-bild-schon-vorhanden";
    }
    
    
    /**
     * Weiterleitungs-Seite für Fehler (v.a. I/O-Fehler) beim Upload eines Bilds.
     * 
     * @return Name von Template-Datei
     */    
    @GetMapping("/upload-fehler")
    public String fehlerBeimHochladen() {
        
        return "upload-fehler";
    }    


}

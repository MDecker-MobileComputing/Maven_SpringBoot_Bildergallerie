package de.eldecker.dhbw.spring.bildergallerie.web;

import java.io.IOException;

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
     *                                Weiterleitungsseite
     * 
     * @return Weiterleitung auf Folgeseite für Erfolg oder Fehler
     */
    @PostMapping("/bild")
    public String bildHochladen( @RequestParam("bild")  MultipartFile bild,            
                                 @RequestParam("titel") String        titel,
                                 RedirectAttributes attributeWeiterleitung ) {
        
        if ( bild.isEmpty() ) {
            
            LOG.warn( "Leeres Bild hochgeladen." );
            return "redirect:keinBild";
        }
                                    
        final long   bytes       = bild.getSize();
        final int    kByte       = (int) ( bytes / 1024 );
        final String dateiName   = bild.getOriginalFilename();
        final String contentType = bild.getContentType();
        final String titelNormal = titel.trim();

        LOG.info( "Bild hochgeladen: DateiName=\"{}\", ContentType=\"{}\", {} kBytes, Titel=\"{}\"", 
                  dateiName, contentType, kByte, titelNormal ); 
        
        try {
        
            final byte[] byteArray = bild.getBytes(); // throws IOException
            
            final long id = _bildService.bildHochladen( titelNormal, byteArray );
            
            LOG.info( "Bild mit Titel \"{}\" unter ID {} in DB gespeichert.", dateiName, id );
            
            attributeWeiterleitung.addFlashAttribute( "dateigroesse_kb", kByte );

            return "redirect:upload-erfolg";
            
        }
        catch ( IOException ex ) {
            
            LOG.error( "Fehler bei Zugriff auf hochgeladene Bilddaten.", ex );
            return "redirect:upload-fehler";
        }                       
    }    

    
    /**
     * Weiterleitungs-Seite für erfolgreichen Upload anzeigen.
     * 
     * @return Name von Template-Datei "hochgeladen.html"
     */
    @GetMapping("/upload-erfolg")
    public String erfolg() {
        
        return "upload-erfolg";
    }

    
    /**
     * Weiterleitungs-Seite für Versuch ein "leeres" Bild hochzuladen anzeigen.
     * 
     * @return Name von Template-Datei "keinbild.html"
     */    
    @GetMapping("/upload-keinBild")
    public String keinBild() {
        
        return "upload-fehler-keinBild";
    }
    
    
    /**
     * Weiterleitungs-Seite für Fehler (v.a. I/O-Fehler) beim Upload eines Bilds.
     * 
     * @return Name von Template-Datei "upload-fehler.html"
     */    
    @GetMapping("/upload-fehler")
    public String fehlerBeimHochladen() {
        
        return "upload-fehler";
    }    


}

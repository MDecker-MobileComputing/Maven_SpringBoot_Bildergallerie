package de.eldecker.dhbw.spring.bildergallerie.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
 

/**
 * Klasse stellt REST-Endpunkte bereit, die von JavaScript im Frontend (HTML)
 * angesprochen werden. 
 */
@Controller
@RequestMapping( "/app/" )
public class DateiUploadController {

    private static final Logger LOG = LoggerFactory.getLogger( DateiUploadController.class );
    
    /**
     * Nimmt hochgeladenes Bild entgegen.
     * 
     * @param bild Hochgeladenes Bild
     * 
     * @param titel Vom Nutzer eingegebener Name für das Bild
     * 
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden.
     * 
     * @return Weiterleitung auf Folgeseite für Erfolg oder kein Bild hochgeladen
     */
    @PostMapping("/bild")
    public String bildHochladen( @RequestParam("bild")  MultipartFile bild,            
                                 @RequestParam("titel") String        titel ) {
        
        if ( bild.isEmpty() ) {
            
            LOG.warn( "Leeres Bild hochgeladen." );
            return "redirect:keinBild";
        }
                                    
        final int    kByte       = (int) ( bild.getSize() / 1024 );
        final String dateiName   = bild.getOriginalFilename();
        final String contentType = bild.getContentType();
        final String titelNormal = titel.trim();
        
        LOG.info( "Bild hochgeladen: DateiName=\"{}\", ContentType=\"{}\", {} kBytes, Titel=\"{}\"", 
                  dateiName, contentType, kByte, titelNormal ); 

        return "redirect:erfolg";
    }    

    
    /**
     * Seite für erfolgreichen Upload anzeigen
     * 
     * @return Name von Template-Datei "hochgeladen.html"
     */
    @GetMapping("/erfolg")
    public String erfolg() {
        
        return "hochgeladen";
    }

    
    /**
     * Seite für Versuch ein "leeres" Bild hochzuladen anzeigen.
     * 
     * @return Name von Template-Datei "keinbild.html"
     */    
    @GetMapping("/keinBild")
    public String keinBild() {
        
        return "keinbild";
    }


}

package de.eldecker.dhbw.spring.bildergallerie.web;

import static java.lang.Long.parseLong;
import static java.lang.String.format;

import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.eldecker.dhbw.spring.bildergallerie.db.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;


/**
 * Controller (kein RestController!), der die Anfragen für die Thymeleaf-Views bearbeitet.
 * Alle Pfade beginnen mit {@code /app/}.
 * Die Mapping-Methoden geben immer den Namen (ohne Datei-Endung) der darzustellenden Template-Datei
 * zurück, der im Ordner {@code src/main/resources/templates/} gesucht wird. 
 */ 
@Controller
@RequestMapping( "/app/" )
public class ThymeleafController {
	
	private static final Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );
	
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;

    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public ThymeleafController( BildRepository bildRepo ) {
    	
    	_bildRepo = bildRepo;
    }
	
    
	/**
	 * Einzelnes Bild anhand ID anzeigen.
	 * 
	 * @param model  Objekt, in das die Werte für die Platzhalter in der Template-Datei
	 *               geschrieben werden.
	 *               
	 * @param idStr Pfadparameter "id" mit ID (Primärschlüssel) des anzuzeigenden
	 *              Bildes
	 * 
	 * @return Template-Datei "anzeige-einzelbild" oder (wenn {@code idStr} nicht nach
	 *         {@code long} geparst werden kann oder es kein Bild mit {@code idStr}
	 *         gibt) "anzeige-einzelbild-fehler"
	 */
    @GetMapping( "/einzelbild/{id}" )
    public String hauptseiteAnzeigen( Model model,
    		                          @PathVariable("id") String idStr ) {
    	
    	LOG.info( "Anzeige von Einzelbild mit ID=\"{}\" angefordert.", idStr );
    	
    	try {
    	
    		final long id = parseLong( idStr );
    		
    		final Optional<BildEntity> bildOptional = _bildRepo.findById( id );
    		if ( bildOptional.isEmpty() ) {
    			
        		final String fehlertext = 
        				format( "Bild mit ID %d nicht gefunden.", id );
    			
    			LOG.error( fehlertext );
    			
        		model.addAttribute( "fehlermeldung", fehlertext );
        		
        		return "anzeige-einzelbild-fehler";
    		}
    		
    		final BildEntity bild = bildOptional.get();
    		
        	model.addAttribute( "bild_titel"    , bild.getTitel()              );
        	model.addAttribute( "bild_datumzeit", bild.getZeitpunktErzeugung() );
        	model.addAttribute( "bild_kBytes"   , bild.getBildGroesseKBytes()  );
    	}
    	catch ( NumberFormatException ex ) {
    	
    		final String fehlertext = 
    				format( "Für Einzelbildanzeige übergebene ID=\"%s\" ist kein gültiger long-Wert.", 
    						idStr );
    		
    		LOG.error( fehlertext );
    		
    		model.addAttribute( "fehlermeldung", fehlertext );
    		
    		return "anzeige-einzelbild-fehler";
    	}
    	
    	return "anzeige-einzelbild";
    }
	
}

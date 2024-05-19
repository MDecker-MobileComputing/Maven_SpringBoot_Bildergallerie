package de.eldecker.dhbw.spring.bildergallerie.web;

import static java.lang.String.format;

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
	 * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
	 *              geschrieben werden.
	 *               
	 * @param id Pfadparameter "id" mit ID (Primärschlüssel) des anzuzeigenden
	 *           Bildes
	 * 
	 * @return Template-Datei "anzeige-einzelbild" wenn es kein Bild mit {@code id}
	 *         gibt
	 */
    @GetMapping( "/einzelbild/{id}" )
    public String hauptseiteAnzeigen( Model model,
    		                          @PathVariable("id") long id ) {
    	
    	LOG.info( "Anzeige von Einzelbild mit ID=\"{}\" angefordert.", id );
    	
		final Optional<BildEntity> bildOptional = _bildRepo.findById( id );
		if ( bildOptional.isEmpty() ) {
			
    		final String fehlertext = 
    				format( "Bild mit ID %d nicht gefunden.", id );
			
			LOG.error( fehlertext );
			
    		model.addAttribute( "fehlermeldung", fehlertext );
    		
    		return "anzeige-einzelbild-fehler";
    		
		} else { // Bild gefunden
			
    		final BildEntity bild = bildOptional.get();
    		
        	model.addAttribute( "bild_titel"    , bild.getTitel()              );
        	model.addAttribute( "bild_datumzeit", bild.getZeitpunktErzeugung() );
        	model.addAttribute( "bild_kBytes"   , bild.getBildGroesseKBytes()  );
        	model.addAttribute( "bild_id"       , id                           );
        	
        	LOG.info( "Bild für Einzelanzeige gefunden: {}", bild );
        	
        	return "anzeige-einzelbild";
		}
    }
    
    
    /**
     * Liste von Bildern anzeigen.
     * 
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden. 
     * 
     * @return Template-Datei "bilder-liste
     */
    @GetMapping( "/liste" )
    public String listeAnzeigen( Model model ) {
        
        final Iterable<BildEntity> bilderIterator = _bildRepo.findAll();
        
        model.addAttribute( "bilder_liste", bilderIterator );
        
        return "bilder-liste";
    }
	
}

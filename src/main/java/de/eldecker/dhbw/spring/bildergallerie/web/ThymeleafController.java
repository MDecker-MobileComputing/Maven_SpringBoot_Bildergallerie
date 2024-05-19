package de.eldecker.dhbw.spring.bildergallerie.web;

import static de.eldecker.dhbw.spring.bildergallerie.logik.SortierAttributEnum.MIME_TYP;
import static de.eldecker.dhbw.spring.bildergallerie.logik.SortierAttributEnum.TITEL;
import static de.eldecker.dhbw.spring.bildergallerie.logik.SortierAttributEnum.ZEIT;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;
import de.eldecker.dhbw.spring.bildergallerie.logik.BildService;
import de.eldecker.dhbw.spring.bildergallerie.logik.SortierAttributEnum;
import de.eldecker.dhbw.spring.bildergallerie.logik.TagService;


/**
 * Controller (kein RestController!), der die Anfragen für die Thymeleaf-Views bearbeitet.
 * Alle Pfade beginnen mit {@code /app/}.
 * <br><br>
 * 
 * Die Mapping-Methoden geben immer den Namen (ohne Datei-Endung) der darzustellenden Template-Datei
 * zurück, der im Ordner {@code src/main/resources/templates/} gesucht wird. 
 */ 
@Controller
@RequestMapping( "/app/" )
public class ThymeleafController {
	
	private static final Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );
	
	
    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;
    
    
    /** Service-Bean mit Geschäftslogik für Bilder. */
    private final BildService _bildService;
    
    /** Service-Bean mit Geschäftslogik von Tags. */
    private final TagService _tagService;
    

    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public ThymeleafController( BildRepository bildRepo,
                                BildService bildService,
                                TagService tagService ) {                                
    	
    	_bildRepo    = bildRepo;
    	_bildService = bildService;
    	_tagService  = tagService; 
    }
	
    
	/**
	 * Einzelnes Bild anhand ID anzeigen.
	 * 
	 * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
	 *              geschrieben werden.
	 *               
	 * @param id Pfadparameter "id" mit ID (Primärschlüssel) des anzuzeigenden Bildes           
	 * 
	 * @return Template-Datei "anzeige-einzelbild" wenn es kein Bild mit {@code id}
	 *         gibt
	 */
    @GetMapping( "/einzelbild/{id}" )
    public String bildAnzeigen( Model model,
    		                    @PathVariable("id") long id ) {
    	
    	LOG.info( "Anzeige von Einzelbild mit ID=\"{}\" angefordert.", id );
    	
		final Optional<BildEntity> bildOptional = _bildRepo.findById( id );
		if ( bildOptional.isEmpty() ) {
			
    		final String fehlertext = format( "Bild mit ID %d nicht gefunden.", id );     				
			
			LOG.error( fehlertext );
			
    		model.addAttribute( "fehlermeldung", fehlertext );
    		
    		return "anzeige-einzelbild-fehler";
    		
		} else { // Bild gefunden
			
    		final BildEntity bild = bildOptional.get();
    		
        	model.addAttribute( "bild_titel"    , bild.getTitel()              );
        	model.addAttribute( "bild_datumzeit", bild.getZeitpunktErzeugung() );
        	model.addAttribute( "bild_kBytes"   , bild.getBildGroesseKBytes()  );
        	model.addAttribute( "bild_id"       , id                           );
        	model.addAttribute( "bild_tags"     , bild.getTags()               );
        	
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
     * @param sortiertNach URL-Parameter für Angabe des Attributs, nach dem die Liste
     *                     sortiert werden soll. Gültige Werte:                        
     *                     {@code zeit} (Default-Wert), {@code typ}, {@code titel}.
     *                     Für ungültigen Wert wird eine Fehlerseite angezeigt.              
     * 
     * @return Template-Datei "bilder-liste" oder "bilder-liste-fehler"
     */
    @GetMapping( "/liste" )
    public String listeAnzeigen( Model model,
                                 @RequestParam(defaultValue = "zeit") String sortiertNach ) {
                
        sortiertNach = sortiertNach.trim().toLowerCase();
        
       SortierAttributEnum sortierAttribut = ZEIT;

       switch ( sortiertNach ) {
       
           case "zeit" : sortierAttribut = ZEIT    ; break;
           case "typ"  : sortierAttribut = MIME_TYP; break;
           case "titel": sortierAttribut = TITEL   ; break;
           default:
               final String fehlerText = 
                   format( "Ungültiger Wert \"%s\" für URL-Parameter \"sortiertNach\".", sortiertNach );                        
               LOG.error( fehlerText );       
               model.addAttribute( "fehlertext", fehlerText );       
               return "bilder-liste-fehler";                                      
       }
                  
       final List<BildEntity> bilderIterator = _bildService.getBildListe( sortierAttribut );
       model.addAttribute( "bilder_liste", bilderIterator );        
       
       return "bilder-liste";
    }
	
    
    /**
     * Liste aller Tags anzeigen.
     * 
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden. 
     *              
     * @return Template-Datei "tag-liste"              
     */
    @GetMapping( "/tags" )
    public String tagsAnzeigen( Model model ) {
           
        final List<TagEntity> tagListe = _tagService.getListeTags();
        
        model.addAttribute( "tag_liste", tagListe );
        
        return "tag-liste";
    }
    
}

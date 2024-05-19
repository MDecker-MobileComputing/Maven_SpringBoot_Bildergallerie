package de.eldecker.dhbw.spring.bildergallerie.logik;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.bildergallerie.db.TagRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;


/**
 * Diese Klasse enthält die Methoden mit der Geschäftslogik für 
 * die Arbeit mit Tags (Schildchen).
 */
@Service
public class TagService {

    /** Repo-Bean für Zugriff auf Datenbanktabelle mit den Tags. */
    private final TagRepository _tagRepo;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public TagService( TagRepository tagRepo ) {
        
        _tagRepo = tagRepo;
    }
    
    
    /**
     * Liefert Liste aller Tags zurück.
     * 
     * @return Liste der Tags, aufsteigend nach Name sortiert
     */
    public List<TagEntity> getListeTags() {
        
        final Sort sort = Sort.by( "name" );
        
        return _tagRepo.findAll( sort );
    }
}

package de.eldecker.dhbw.spring.bildergallerie.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;


/**
 * Zur Laufzeit wird von <i>Spring Data JPA</i> automatisch eine Implementierung
 * dieses Interface erzeugt. 
 * <br><br>
 * 
 * Die Klasse ist mit {@code RepositoryRestResource} annotiert, deshalb stehen
 * die Tags unter dem Pfad {@code http://localhost:8080/tags } zur Verfügung.
 * Hierfür muss in der Datei {@code pom.xml} die Abhängigkeit
 * {@code spring-boot-starter-data-rest} hinzugefügt werden.
 */
@RepositoryRestResource(path = "tags")
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    /**
     * Eigene Sortiermethode mit JPQL, um bei alphabetischer Sortierung nach
     * Name Groß-/Kleinschreibung zu ignorieren.
     * <br><br>
     * 
     * Es kann auch wie folgt mit der automatisch erzeugen Methoden sortiert 
     * werden, aber dann kommen Tags, deren Namen mit einem Kleinbuchstaben  
     * anfängt, nach Tags mit Namen mit Großbuchstaben:
     * <pre>
     * Sort sort = Sort.by( "name" );
     * List<TagEntity> liste = _tagRepo.findAll( sort );               
     * 
     * @return Liste aller {@link TagEntity}-Objekte, alphabetisch aufsteigend sortiert
     */
    @Query("SELECT t FROM TagEntity t ORDER BY LOWER(t.name)")
    List<TagEntity> findAllSortByNameIgnoreCase();
    
    
    /**
     * {@link TagEntity}-Objekt mit {@code name} suchen. 
     * <br><br>
     * 
     * Implementierung dieser Methode wird anhand Namen zur Laufzeit automatisch
     * erzeugt.
     * 
     * @param name Anzeigename des Tags, z.B. "Hund"
     * 
     * @return Optional mit {@link TagEntity}-Objekt wenn Tag mit {@code name}
     *         gefunden, sonst leer
     */
    Optional<TagEntity> findByName( String name );

}

package de.eldecker.dhbw.spring.bildergallerie.db;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


/**
 * Zur Laufzeit wird von <i>Spring Data JPA</i> automatisch eine Implementierung
 * dieses Interface erzeugt. 
 */
public interface BildRepository extends CrudRepository<BildEntity, Long> {

    /**
     * Bild anhand Hashwert suchen.
     * 
     * @param hash Hashwert des Bildes
     * 
     * @return Optional mit Bild-Entity, falls ein Bild mit dem Hashwert
     *         in der Datenbank existiert, sonst leeres Optional
     */
    Optional<BildEntity> findByHash( String hash );

}
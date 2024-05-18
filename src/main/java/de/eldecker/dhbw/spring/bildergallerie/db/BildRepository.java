package de.eldecker.dhbw.spring.bildergallerie.db;

import org.springframework.data.repository.CrudRepository;


/**
 * Zur Laufzeit wird von <i>Spring Data JPA</i> automatisch eine Implementierung
 * dieses Interface erzeugt. 
 */
public interface BildRepository extends CrudRepository<BildEntity, Long> {
}
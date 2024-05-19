package de.eldecker.dhbw.spring.bildergallerie.db;

import org.springframework.data.jpa.repository.JpaRepository;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;

/**
 * Zur Laufzeit wird von <i>Spring Data JPA</i> automatisch eine Implementierung
 * dieses Interface erzeugt. 
 */
public interface TagRepository extends JpaRepository<TagEntity, Long> {

}

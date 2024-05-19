package de.eldecker.dhbw.spring.bildergallerie.db;

import org.springframework.data.jpa.repository.JpaRepository;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;

import java.util.Optional;


/**
 * Zur Laufzeit wird von <i>Spring Data JPA</i> automatisch eine Implementierung
 * dieses Interface erzeugt. 
 */
public interface BildRepository extends JpaRepository<BildEntity, Long> {

    /**
     * Bild anhand Hash-Wert suchen (Attribut "Hash" für die Tabelle hat deshalb
     * einen Index). "Spring Data JPA" erkennt anhand dem Methodennamen was
     * die Methode tun soll.
     * 
     * @param hash Hashwert des Bildes
     * 
     * @return Optional mit Bild-Entity, falls ein Bild mit dem Hashwert
     *         in der Datenbank existiert, sonst leeres Optional
     */
    Optional<BildEntity> findByHash( String hash );

}
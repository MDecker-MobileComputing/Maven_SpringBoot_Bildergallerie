package de.eldecker.dhbw.spring.bildergallerie.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;

import java.util.Optional;


/**
 * Zur Laufzeit wird von <i>Spring Data JPA</i> automatisch eine Implementierung
 * dieses Interface erzeugt. 
 * <br><br>
 * 
 * Die Klasse ist mit {@code RepositoryRestResource} annotiert, deshalb stehen
 * die Metadaten der Bilder unter dem Pfad {@code http://localhost:8080/bilder } 
 * zur Verfügung.
 * Hierfür muss in der Datei {@code pom.xml} die Abhängigkeit
 * {@code spring-boot-starter-data-rest} hinzugefügt werden. 
 * <br><br>
 * 
 * In Klasse {@link BildEntity} wurden das Attribut {@code bild} und die
 * Methode {@link BildEntity#getBildBytes()} mit der Annotation 
 * {@code JsonIgnore} versehen.
 */
@RepositoryRestResource(path = "bilder")
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
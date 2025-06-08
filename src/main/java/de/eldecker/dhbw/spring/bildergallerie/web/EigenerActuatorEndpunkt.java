package de.eldecker.dhbw.spring.bildergallerie.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.TagRepository;


/**
 * Eigener Actuator-Endpunkt für Bereitstellung von Metriken für Anzahl von
 * Datensätzen in der Datenbank.
 * <br><br>
 *
 * Damit dieser Actuator-Endpunkt über HTTP erreicht werden, muss in der Datei
 * {@code application.properties} die ID des Endpunkts "datensaetze" in der Liste
 * der freigebenen Endpunkte eingetragen sein:
 * {@code management.endpoints.web.exposure.include=datensaetze,metric,... }
 * <br><br>
 *
 * Der Endpunkt ist dann unter der folgenden URL über HTTP-GET erreichbar:
 * {@code http://localhost:8080/actuator/datensaetze }
 */
@Component
@Endpoint(id = "datensaetze")
public class EigenerActuatorEndpunkt {

    /**
     * Record-Klasse als Rückgabewert für den Endpunkt, wird nach JSON
     * serialisiert.
     */
    public record DatensaetzeCount( int anzahlBilder, int anzahlTags ) {}

    /** Repo-Bean für Bild-Tabelle in DB. */
    private final BildRepository _bildRepo;

    /** Repo-Bean für Tag-Tabelle in DB. */
    private final TagRepository _tagRepo;


    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public EigenerActuatorEndpunkt( BildRepository bildRepo,
                                    TagRepository tagRepo ) {

        _bildRepo = bildRepo;
        _tagRepo  = tagRepo;
    }


    /**
     * Actuator-Endpunkt, liefert die Anzahl der Datensätze in den beiden
     * Haupt-Tabellen der Anwendung zurück.
     *
     * @return Objekt mit Anzahl der Bilder und Tags, wird nach JSON serialisiert.
     */
    @ReadOperation
    public DatensaetzeCount datensaetzeCount() {

        final int anzahlBilder = (int) _bildRepo.count();
        final int anzahlTags   = (int) _tagRepo.count();
        return new DatensaetzeCount( anzahlBilder, anzahlTags );
    }

}

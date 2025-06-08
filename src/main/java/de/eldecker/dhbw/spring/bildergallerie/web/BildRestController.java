package de.eldecker.dhbw.spring.bildergallerie.web;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.eldecker.dhbw.spring.bildergallerie.db.BildRepository;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;


/**
 * RestController zu Bereitstellung der Bilder (Binärdaten!).
 */
@RestController
@RequestMapping( "/app/" )
public class BildRestController {

    private static final Logger LOG = LoggerFactory.getLogger( BildRestController.class );

    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Bildern. */
    private final BildRepository _bildRepo;


    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public BildRestController( BildRepository bildRepo ) {

        _bildRepo = bildRepo;
    }


    /**
     * Einzelnes Bild als Binärdatei bereitstellen.
     *
     * @param id Primärschlüssel von Bild, das zurückgeliefert werden soll.
     *
     * @return HTTP-Status-Code 200 und Bild als Binärdatei; HTTP-Status-Code 404 wenn
     *         Bild nicht gefunden, HTTP-Status-Code 500 wenn DB-Fehler beim Zugriff
     *         auf Bild.
     */
    @GetMapping(value = "/bild/{id}")
    public ResponseEntity<byte[]> getBild( @PathVariable Long id ) {

        final Optional<BildEntity> bildOptional = _bildRepo.findById( id );
        if ( bildOptional.isEmpty() ) {

            LOG.error( "Bild mit ID={} als Binärdatei angefordert, wurde aber nicht gefunden.", id );
            return ResponseEntity.notFound().build();
        }

        final BildEntity bildEntity = bildOptional.get();

        final int anzahlBytes = bildEntity.getBildGroesseBytes();
        if ( anzahlBytes <= 0 ) {

            LOG.error( "Bild mit ID={} als Binärdatei angefordert, aber AnzBytes={}.", id, anzahlBytes );
            return ResponseEntity.internalServerError().build();
        }

        final byte[] blobAsBytes = bildEntity.getBildBytes();

        final MediaType mediaType = MediaType.valueOf( bildEntity.getMimeTyp() );

        return ResponseEntity.ok()
                             .contentType( mediaType )
                             .body( blobAsBytes );
    }

}

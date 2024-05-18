package de.eldecker.dhbw.spring.bildergallerie.db;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;

import java.sql.Blob;
import java.sql.SQLException;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Datenbank-Entität für ein Bild.
 */
@Entity
@Table( name = "BILDER" )
public class BildEntity {

    private final static Logger LOG = LoggerFactory.getLogger( BildEntity.class );

    /**
     * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
     * gibt es auch keinen Setter für dieses Attribut.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long _id;

    /** Titel/Name des Bildes, z.B. "Katze im Garten". */
    @Column(name = "titel")
    private String _titel;

    /** Zeitpunkt (Datum + Uhrzeit) des Uploads des Bilds. */
    @Column(name = "zeitpunkt_erzeugung")
    private LocalDateTime _zeitpunktErzeugung;

    /** Bild als "Binary Large Object" (BLOB). */
    @Lob
    private Blob _bild;


    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public BildEntity() {

        _titel              = "";
        _zeitpunktErzeugung = now();
    }


    /**
     * Getter für Primärschlüssel
     *
     * @return ID des Bilds
     */
    public Long getId() {

        return _id;
    }


    /**
     * Getter für den Titel des Bilds.
     *
     * @return Name/Titel des Bilds, z.B. "Röhrender Hirsch"
     */
    public String getTitel() {

        return _titel;
    }


    /**
     * Setter für den Titel des Bilds.
     *
     * @param titel Name/Titel des Bilds, z.B. "Röhrender Hirsch"
     */
    public void setTitel( String titel ) {

        _titel = titel;
    }


    /**
     * Setter für den Zeitpunkt des Uploads des Bilds.
     *
     * @param zeitpunktErzeugung Zeitpunkt des Uploads
     */
    public void setZeitpunktErzeugung( LocalDateTime zeitpunktErzeugung ) {

        _zeitpunktErzeugung = zeitpunktErzeugung;
    }


    /**
     * Getter für den Zeitpunkt des Uploads des Bilds.
     *
     * @return Zeitpunkt des Uploads
     */
    public LocalDateTime getZeitpunktErzeugung() {

        return _zeitpunktErzeugung;
    }


    /**
     * Methode erzeugt eine String-Repräsentation des Objekts.
     *
     * @return String mit Name/Titel des Bilds und Größe des Bilds
     *         in Kilobyte (KB).
     */
    @Override
    public String toString() {

        int bildGroesseKB = -1;
        if ( _bild == null ) {

            try {
                bildGroesseKB = (int)( _bild.length() / 1024 );
            }
            catch ( SQLException ex ) {

                LOG.error( "Fehler beim Ermitteln der Bildgröße", ex );
            }
        }

        return "Bild \"" + _titel + "\", " + bildGroesseKB + " KB";
    }


    /**
     * Methode gibt Hashwert für das Objekt zurück.
     *
     * @return Hashwert des Objekts, in den alle Attribute bis auf
     *         den Primärschlüssel einfließen.
     */
    @Override
    public int hashCode() {

        return Objects.hash( _titel,
                             _zeitpunktErzeugung,
                             _bild
                           );
    }


    /**
     * Methode prüft, ob das übergebene Objekt gleich diesem Objekt ist.
     *
     * @param obj Objekt, mit dem dieses Objekt verglichen wird
     *
     * @return  {@code true}, wenn die Objekte gleich sind, sonst {@code false}
     */
    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) {

            return true;
        }
        if ( obj == null ) {

            return false;
        }
        if ( getClass() != obj.getClass() ) {

            return false;
        }

        final BildEntity other = (BildEntity) obj;

        return Objects.equals( _titel             , other._titel              ) &&
               Objects.equals( _zeitpunktErzeugung, other._zeitpunktErzeugung );
    }


}
package de.eldecker.dhbw.spring.bildergallerie.db;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;

import java.sql.Blob;
import java.sql.SQLException;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Datenbank-Entität für ein Bild.
 * <br><br>
 * 
 * Die Objektvariablen sind NICHT mit {@code COLUMN} annotiert, es werden also die
 * Variablennamen als Spaltennamen in der Tabelle {@code BILDER} übernommen.
 * <br><br>
 * 
 * Siehe auch 
 * <a href="https://thorben-janssen.com/mapping-blobs-and-clobs-with-hibernate-and-jpa/">diese Seite</a>
 * für Verwendung von BLOB-Spalten bei JPA.
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
    private Long id;

    /** Titel/Name des Bildes, z.B. "Katze im Garten". */
    private String titel;

    /** Zeitpunkt (Datum + Uhrzeit) des Uploads des Bilds. */
    private LocalDateTime zeitpunktErzeugung;

    /** Bild als "Binary Large Object" (BLOB). */
    @Lob
    private Blob bild;
    
    /** Hashwert des Bildes (z.B. MD5-Hash), damit man schnell bereits vorhandene Bilder erkennen kann. */
    private String hash;


    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public BildEntity() {

        titel              = "";
        hash               = "";
        zeitpunktErzeugung = now();
    }


    /** 
     * Neues Bildobjekt erzeugen, als Zeitpunkt wird die aktuelle
     * Systemzeit gesetzt. 
     * 
     * @param titel Names des Bild (von Nutzer eingegeben)
     * 
     * @param bild Bild (Binärdaten); muss mit {@code BlobProxy.generateProxy()}
     *             erzeugt worden sein
     * 
     * @param hash Hashwert von {@code bild}
     */
    public BildEntity( String titel, Blob bild, String hash ) {
    
        this.titel = titel;
        this.bild  = bild;
        this.hash  = hash;
        
        zeitpunktErzeugung = now();
    }
    
    
    /**
     * Getter für Primärschlüssel
     *
     * @return ID des Bilds
     */
    public Long getId() {

        return id;
    }


    /**
     * Getter für den Titel des Bilds.
     *
     * @return Name/Titel des Bilds, z.B. "Röhrender Hirsch"
     */
    public String getTitel() {

        return titel;
    }


    /**
     * Setter für den Titel des Bilds.
     *
     * @param titel Name/Titel des Bilds, z.B. "Röhrender Hirsch"
     */
    public void setTitel( String titel ) {

        this.titel = titel;
    }


    /**
     * Setter für eigentliches Bild (Binärdaten als BLOB).
     * 
     * @param bild Bild ; muss mit {@code BlobProxy.generateProxy()}
     *             erzeugt worden sein
     */
    public void setBild( Blob bild ) {
        
        this.bild = bild;
    }

    
    /**
     * Getter für eigentliches Bild (Binärdaten als BLOB).
     * 
     * @return Bild 
     */
    public Blob getBild() {
        
        return bild;
    }
    
    
    /**
     * Setter für Hashwert von Bild.
     * 
     * @param hash Hashwert von Bild, z.B. MD5-Hash
     */
    public void setHash( String hash ) {
        
        this.hash = hash;
    }

    
    /**
     * Getter für Hashwert von Bild.
     * 
     * @return Hashwert von Bilder, z.B. MD5-Hash
     */
    public String getHash() {
        
        return hash;
    }

    
    /**
     * Setter für den Zeitpunkt des Uploads des Bilds.
     *
     * @param zeitpunktErzeugung Zeitpunkt des Uploads
     */
    public void setZeitpunktErzeugung( LocalDateTime zeitpunktErzeugung ) {

        this.zeitpunktErzeugung = zeitpunktErzeugung;
    }


    /**
     * Getter für den Zeitpunkt des Uploads des Bilds.
     *
     * @return Zeitpunkt des Uploads
     */
    public LocalDateTime getZeitpunktErzeugung() {

        return zeitpunktErzeugung;
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
        if ( bild == null ) {

            try {
                
                bildGroesseKB = (int)( bild.length() / 1024 );
            }
            catch ( SQLException ex ) {

                LOG.error( "Fehler beim Ermitteln der Bildgröße", ex );
            }
        }

        return "Bild \"" + titel + "\", " + bildGroesseKB + " KB";
    }


    /**
     * Methode gibt Hashwert für das Objekt zurück.
     *
     * @return Hashwert des Objekts
     */
    @Override
    public int hashCode() {

        return Objects.hash( titel,
                             zeitpunktErzeugung, 
                             hash );
                           
    }


    /**
     * Methode prüft, ob das übergebene Objekt gleich diesem Objekt ist.
     * Für das eigentliche Bild wird der Hash-Wert verglichen.
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

        return Objects.equals( titel             , other.titel              ) &&
               Objects.equals( zeitpunktErzeugung, other.zeitpunktErzeugung ) &&
               Objects.equals( hash              , other.hash               );
    }


}
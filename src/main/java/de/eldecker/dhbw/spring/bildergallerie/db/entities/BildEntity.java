package de.eldecker.dhbw.spring.bildergallerie.db.entities;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;

import java.sql.Blob;
import java.sql.SQLException;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
 * Mit "Hilfsmethode" bezeichnete Methoden sind Getter, die (berechnete) Werte zurückgeben,
 * also nicht Getter für persistierte Attribute sind.
 * <br><br>
 * 
 * Siehe auch 
 * <a href="https://thorben-janssen.com/mapping-blobs-and-clobs-with-hibernate-and-jpa/">diese Seite</a>
 * für Verwendung von BLOB-Spalten bei JPA.
 */
@Entity
@Table(name = "BILDER", indexes = {@Index(name = "index_hash", columnList = "hash")})
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
    
    /** 
     * Hashwert des Bildes (z.B. MD5-Hash), damit man schnell bereits vorhandene Bilder erkennen kann;
     * für dieses Attribut gibt es einen DB-Index. 
     */
    private String hash;
    
    /** MIME-Typ des Bildes, z.B. "image/jpeg". Wird benötigt, damit Browser das Bild richtig darstellt. */
    private String mimeTyp;
    

    /**
     * Ein Bild kann keine, ein oder mehrere Tags zugeordnet bekommen.
     * Da ein Tag auch mehreren Bildern zugeordnet sein kann, brauchen wir eine "Many-to-Many"-Relation,
     * für die auch eine Join-Tabelle in der DB erforderlich ist.
     * Siehe auch Attribut "bilder" in {@Link TagEntity}.
     */
    @ManyToMany 
    @JoinTable( name               = "tag_zu_bild",      
                joinColumns        = @JoinColumn(name = "bild_id"),
                inverseJoinColumns = @JoinColumn(name = "tag_id") )
    private Set<TagEntity> tags;


    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public BildEntity() {

        titel              = "";
        hash               = "";
        mimeTyp            = "";
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
     * 
     * @param hash mimeTyp MIME-Typ, z.B. "image/jpeg"
     */
    public BildEntity( String titel, Blob bild, String hash, String mimeTyp ) {
    
        this.titel   = titel;
        this.bild    = bild;
        this.hash    = hash;
        this.mimeTyp = mimeTyp;
        
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
     * Hilfsmethode: Größe Bild in Bytes abfagen.
     * 
     * @return Größe des Bilds in Byte, {@code 0} wenn Bild noch nicht vorhanden,
     *         {@code -1} wenn {@code SQLException} bei Zugriff auf Bild.
     *             
     */
    public int getBildGroesseBytes() {
    	
    	if ( getBild() == null ) {
    		
    		LOG.warn( "Zugriff auf Bildgröße von Bild mit ID={}, aber Bild noch nicht vorhanden", 
    				  getId() );
    		return 0;
    	}
    	
    	try {
    		
    		return (int) getBild().length(); // length() throws SQLException
    	}
    	catch ( SQLException ex ) {
    	
    		LOG.error( "Exception beim Auslesen von Bildgröße für Bild mit ID={}.", 
    				   getId(), ex ) ;
    		return -1;
    	}
    }
    
    
    /**
     * Hilfsmethode: Größe Bild in kBytes abfagen.
     * <br><br>
     * 
     * Intern wird die Methode {@link #getBildGroesseBytes()} verwendet.
     * 
     * @return Größe des Bildes in kByte oder {@code -1} wenn die Größe des
     *         Bildes wegen {@code SQLException} nicht ausgelesen werden kann.
     *         Wenn das Bild nicht vorhanden ist, dann wird {@code 0}
     *         zurückgegeben.
     */
    public int getBildGroesseKBytes() {
     
    	final int anzahlBytes = getBildGroesseBytes();
    	if ( anzahlBytes <= 0 ) {
    		
    		return anzahlBytes;
    		
    	} else {
    		
    		return anzahlBytes / 1024;
    	}
    }
    
    
    /**
     * Hilfsmethode: Bild als Byte-Array zurückgeben.
     * 
     * @return Byte-Array mit Binärdaten von Bild, oder leerer Array
     *         wenn Fehler aufgetreten (aber nicht {@code null}).
     */
    public byte[] getBildBytes() {
    	
    	final int anzahlBytes = getBildGroesseBytes();
    	if ( anzahlBytes <= 0 ) {
    		
    		return new byte[0];
    	}
    	
    	try {
    	
    		final Blob blob = getBild();
    		byte[] blobAsBytes = blob.getBytes( 1, anzahlBytes );
    		
    		return blobAsBytes;
    	}
    	catch ( SQLException ex ) {
    		
    		LOG.error( "DB-Fehler bei Zugriff auf Byte-Array von Bild mit ID={}.", 
    				   getId(), ex );
    		return new byte[0];
    	}
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
     * Getter für MIME-Typ von Bild, z.B. "image/jpeg"
     * 
     * @return MIME-Typ von Bild
     */
    public String getMimeTyp() {
        
        return mimeTyp;
    }
    
    
    /**
     * Setter für MIME-Typ von Bild, z.B. "image/jpeg"
     * 
     * @param mimeTyp MIME-Typ von Bild
     */
    public void setMimeTyp( String mimeTyp ) {
        
        this.mimeTyp = mimeTyp;
    }

    
    /**
     * Hilfsmethode: Gibt String mit Bildtyp statt MIME-Typ zurück,
     * also z.B. "jpeg" statt "image/jpeg".
     * 
     * @return Typ des Bildes: "jpeg", "png", "gif" oder "svg"
     */
    public String getBildTyp() {
    
        final String mimeTyp = getMimeTyp();
        return mimeTyp.replaceFirst( "image/", "" );
    }
    
        
    /**
     * Getter für die Tags, die dem aufrufenden Bild zugewiesen sind.
     *  
     * @return Menge der Tags (kann auch leer sein)
     */
    public Set<TagEntity> getTags() {
        
        return tags;
    }


    /**
     * Getter für die Tags, die dem aufrufenden Bild zugewiesen sind.
     * 
     * @param tags Menge der Tags (kann auch leer sein)
     */
    public void setTags( Set<TagEntity> tags ) {
        
        this.tags = tags;
    }
    
    
    /**
     * Hilfsmethode, um dem Bild ein Tag zuzuordnen. 
     * 
     * @param tag Tag, das dem Bild zugeordnet werden soll. 
     * 
     * @return {@code true} wenn dieses Tag dem Bild noch nicht zugeordnet war.
     */
    public boolean addTag( TagEntity tag ) {
        
        if ( tags == null ) {
            
            tags = new HashSet<>( 5 );
            return tags.add( tag );
            
        } else {
            
            return tags.add( tag );
        }
    }


    /**
     * Methode erzeugt eine String-Repräsentation des Objekts.
     *
     * @return String mit Name/Titel des Bilds und Größe des Bilds
     *         in Kilobyte (KB) und MIME-Typ (z.B. "image/jpeg").
     */
    @Override
    public String toString() {

        final int bildGroesseKB = getBildGroesseKBytes();

        return String.format( "Bild \"%s\", %d kByte, MIME-Type=%s.", 
                              titel, bildGroesseKB, mimeTyp );
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
                             hash,
                             mimeTyp );                           
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
               Objects.equals( hash              , other.hash               ) &&
               Objects.equals( mimeTyp           , other.mimeTyp            );
    }


}
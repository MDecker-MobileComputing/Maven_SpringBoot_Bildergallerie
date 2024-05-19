package de.eldecker.dhbw.spring.bildergallerie.helferlein;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Bean, mit der für ein Bild in Form eines Byte-Array der MD5-Hash berechnet werden
 * kann. Damit sollen bereits in der DB gespeicherte Bilder gefunden werden.
 * <br><br>
 * 
 * Achtung: Der MD5-Hash-Algorithmus ist nicht mehr für kryptografische Anwendungen
 * geeignet, da er gebrochen wurde.
 */
@Component
public class MD5Hasher {
    
    private final static Logger LOG = LoggerFactory.getLogger( MD5Hasher.class );
    
    /** Objekt zur Hash-Berechnung mit dem MD5-Algorithmus. */
    private MessageDigest _messageDigest = null;
    
    /**
     * Konstruktor, erzeugt das {@code MessageDigest}-Objekt mit dem MD5-Algorithmus.
     */
    public MD5Hasher() {
        
        try {
        
            _messageDigest = MessageDigest.getInstance( "MD5" ); // throws NoSuchAlgorithmException
            
            LOG.info( "Hash-Algorithmus gefunden: " + _messageDigest );
        }
        catch ( NoSuchAlgorithmException ex ) {
            
            LOG.error( "MD5-Algorithmus steht nicht zur Verfügung.", ex );
        }
    }


    /**
     * Hashwert für Byte-Array berechnen.
     * 
     * @param byteArray Zu verhashende Daten (Binärdaten von Bild)
     * 
     * @return Hash-Wert (128 Bit) als Hex-String oder leerer String, wenn Hash-Algo 
     *         nicht zur Verfügung steht.
     *         Beispiel für MD5-Hash: {@code 158ef3b2b0d2392bd7552cd773323c27}
     */
    public String getHash( byte[] byteArray ) {
    
        if ( _messageDigest != null ) {
            
            _messageDigest.reset();
            
            _messageDigest.update( byteArray );
            
            byte[] hashByteArray = _messageDigest.digest();
            
            return bytesToHex( hashByteArray );
            
        } else {
            
            LOG.warn( "Hash-Wert für {} Bytes konnte nicht berechnet werden weil Hash-Algo nicht gefunden.", 
                      byteArray.length );  
            return "";
        }
    }
    
    
    /**
     * Byte-Array in Hexadezimaldarstellung umwandeln.
     * 
     * @param bytes Byte-Array, der in Hex-Darstellung umgewandelt werden soll
     * 
     * @return Hex-Darstellung von {@code bytes}, z.B. {@code 158ef3b2b0d2392bd7552cd773323c27}
     */
    private static String bytesToHex( byte[] bytes ) {
        
        final StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
    
}

package de.eldecker.dhbw.spring.bildergallerie.db.entities;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


/**
 * Tabelle für Tags (Schildchen, Label) mit denen die Bilder zur Kategorisierung 
 * versehen werden können. Ein Bild kann kein, ein oder mehrere Tags haben. 
 */
@Entity
@Table( name = "TAGS", indexes = {@Index(name = "index_name", columnList = "name")} )
public class TagEntity {

  /**
    * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
    * gibt es auch keinen Setter für dieses Attribut.
    */
   @Id
   @GeneratedValue(strategy = IDENTITY)
   private Long id;
   
   /**
    * Name des Tags, z.B. "Hund"; für dieses Attribut gibt es einen DB-Index.
    */
   private String name;
   
   
   /**
    * Siehe Attribut "tags" in {@link BildEntity}.
    */
   @ManyToMany (mappedBy = "tags")
   private Set<BildEntity> bilder;   
   
   
   /**
    * Default-Konstruktor, wird von JPA benötigt.
    */
   public TagEntity() {
       
       name = "";
   }
   
   /**
    * Konstruktor, um den Namen des Tags festlegen zu können
    */
   public TagEntity( String name ) {
       
       this.name = name;
   }
   
   
   /**
    * Getter für ID, wird von JPA verwaltet/gesetzt.
    * 
    * @return ID (Primärschlüssel).
    */
   public Long getId() {
       
       return id;
   }
   

   /**
    * Getter für Name des Tags, z.B. "Hund".
    * 
    * @return Name des Tags (technischer Name = Anzeigename)
    */
   public String getName() {
        
       return name;
   }
    
   /**
    * Setter für Name des Tags.
    * 
    * @param name Name des Tags, z.B. "Hund" (technischer Name = Anzeigename)
    */
   public void setName(String name) {
        
       this.name = name;
   }
    
      
   /**
    * Getter für die Menge der Bilder, die dieses Tag zugewiesen haben.
    * 
    * @return Menge von Bildern (kann leer sein)
    */
   public Set<BildEntity> getBilder() {
       
       return bilder;
   }

   
   /**
    * String-Repräsentation des Objekts.
    *
    * @return String, enthält u.a. Name des Tags. 
    */
   @Override
   public String toString() {
       
       return "Tag \"" + name +"\"";
   }
   
   
   /**
    * Methode gibt Hashwert für das Objekt zurück.
    *
    * @return Hashwert des Objekts (Hash-Wert von Attribut "name")
    */
   @Override
   public int hashCode() {

       return Objects.hash( name );
   }
   
   
   /**
    * Vergleicht das aufrufende Objekt mit Argument {@code obj}. 
    *   
    * @param obj Objekt, mit dem das aufrufende Objekt verglichen werden soll.
    * 
    * @return {@code false}, wenn {@code obj} den Wert {@code null} hat oder 
    *         nicht derselben Klasse angehört. {@code true} wird nur dann 
    *         zurückgeliefert, werden {@code obj} auch ein {@code TagEntity}-Objekt
    *         ist und denselben Wert für das Attribut "Name" hat.
    *         Das Attribut "ID" wird für den Vergleich nicht berücksichtigt, weil
    *         es erst beim Speichern gesetzt wird.
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

       final TagEntity other = (TagEntity) obj;

       return Objects.equals( name, other.name );
   }

}

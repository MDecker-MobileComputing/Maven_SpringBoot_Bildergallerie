package de.eldecker.dhbw.spring.bildergallerie.logik;


/**
 * Eigener Enum-Typ für Spezifikation des Attributs von {@code BildEntity},
 * nach dem eine Liste von Bildern sortiert werden soll.
 */
public enum SortierAttributEnum {

    ZEIT("zeitpunktErzeugung"),
    MIME_TYP("mimeTyp"),
    TITEL("titel");
    
    /** Technischer Name Attribut von {@code BildEntity}. */
    private final String entityAttributName; 
    
    /**
     * Konstruktor 
     * 
     * @param entityAttributName Technischer Name Attribut
     */
    private SortierAttributEnum( String entityAttributName ) {
        
        this.entityAttributName = entityAttributName;
    }
    
    
    /**
     * Technischer Name des Attributs von {@code BildEntity} nach
     * dem sortiert werden soll.
     * 
     * @return Name des Attributs, z.B. {@code zeitpunktErzeugung}
     */
    public String getEntityAttributName() {
        
        return entityAttributName;
    }
    
}

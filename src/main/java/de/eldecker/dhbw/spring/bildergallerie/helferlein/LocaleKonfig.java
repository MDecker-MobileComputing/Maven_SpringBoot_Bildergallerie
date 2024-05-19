package de.eldecker.dhbw.spring.bildergallerie.helferlein;

import static java.util.Locale.GERMAN;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


/**
 * Konfiguration für Locale (eingestelltes Land, im vorliegenden Fall wegen Verzicht auf
 * übersetzbare Texte nur für Formatierung von Datum/Zeit und Tausendertrennpunkten von 
 * Zahlen).
 */
@Configuration
public class LocaleKonfig {

    
    /**
     * Locale auf {@code GERMAN} festsetzen (für Datumsformatierungen).
     * 
     * @return {@code SessionLocaleResolver} mit {@code DefaultLocal=GERMAN}  
     */
    @Bean
    public LocaleResolver localeResolver() {
        
        final SessionLocaleResolver slr = new SessionLocaleResolver();
        
        slr.setDefaultLocale( GERMAN );
        
        return slr;
    }
    
}

package de.eldecker.dhbw.spring.bildergallerie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Einstiegspunkt der Spring Boot Anwendung, kann für Debugging
 * mit Eclipse auch im Debug-Modus gestartet werden.
 */
@SpringBootApplication
public class BildergallerieApplication {

	public static void main( String[] args ) {

		SpringApplication.run( BildergallerieApplication.class, args );
	}

}

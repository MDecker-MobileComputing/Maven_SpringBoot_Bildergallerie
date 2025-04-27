package de.eldecker.dhbw.spring.bildergallerie.helferlein;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.DELETE;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import de.eldecker.dhbw.spring.bildergallerie.db.entities.BildEntity;
import de.eldecker.dhbw.spring.bildergallerie.db.entities.TagEntity;


/**
 * Schreiboperationen für mit Annotation {@code @RepositoryRestResource}
 * annotierte Repository-Klassen abschalten.
 */
@Configuration
public class RestKonfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration( 
                                RepositoryRestConfiguration config,
                                CorsRegistry cors ) {
        
        ExposureConfiguration exposure = config.getExposureConfiguration();
        exposure.forDomainType( BildEntity.class )
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable( POST, PUT, DELETE ))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable( POST, PUT, DELETE ));
        
        exposure.forDomainType( TagEntity.class )
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable( POST, PUT, DELETE ))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable( POST, PUT, DELETE ));
    }
    
}

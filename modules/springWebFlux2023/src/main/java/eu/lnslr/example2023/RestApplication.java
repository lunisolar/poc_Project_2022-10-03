package eu.lnslr.example2023;

import eu.lunisolar.lava.lang.utils.Collections4U;
import eu.lunisolar.lava.meta.linked_data.SchemaLoader;
import eu.lunisolar.lava.rdf.api.RdfManager;
import eu.lunisolar.lava.rdf.spring.starter.RdfStarterProperties;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Paths;
import java.util.Properties;

@SpringBootApplication
@Import({
        RestConfiguration.class
})
@EnableAspectJAutoProxy
@EnableScheduling
@Log4j2
public class RestApplication {


    public static void main(String... args) {

        SchemaLoader.initializeWithCcl();
        Collections4U.initializeWithCcl();
        RdfManager.initializeWithCcl();

        SpringApplication application = new SpringApplication(RestApplication.class);

        Properties props = new Properties();
        setPropertiesForSpring(props);
        application.setDefaultProperties(props);

        application.run(args);

    }

    private static void setPropertiesForSpring(Properties props) {
        var datasetUrl = getTriplestoreUrl();
        log.info("Triplestore: {}", datasetUrl);
        props.put(RdfStarterProperties.CONNECTION_URL, datasetUrl);
    }

    private static @NonNull String getTriplestoreUrl() {
        return "lava-rdf:jena:memory:/new";
    }


}

package model.integradorsinteclados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

//Modifica @SpringBootApplication para escanear todos los paquetes
@SpringBootApplication(scanBasePackages = {
    "model.integradorsinteclados",
    "application",
    "domain",
    "infrastructure"
})
// linea para decirle dónde buscar las interfaces JPA
@EnableJpaRepositories(basePackages = "infrastructure.persistence.repository")

@EntityScan(basePackages = "infrastructure.persistence.entities")
public class IntegradorSinTecladosApplication {

    public static void main(String[] args) {
        // Establece la zona horaria por defecto a UTC para toda la aplicación.
        // Esto previene el error "invalid value for parameter "TimeZone"" con Postgres.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(IntegradorSinTecladosApplication.class, args);
    }

}

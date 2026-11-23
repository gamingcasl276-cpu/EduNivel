
package bo.edu.edunivel.config;

import bo.edu.edunivel.service.ExamenService;
import bo.edu.edunivel.service.CargaExamenesService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExamenInitializer {

    @Bean
    public CommandLineRunner inicializarExamenes(
            ExamenService examenService,
            CargaExamenesService cargaExamenesService) {

        return args -> {
            examenService.inicializarExamenes();
            cargaExamenesService.cargarExamenes();

            System.out.println("----------------------------------------");
            System.out.println("EDUNIVEL: EXAMENES CARGADOS");
            System.out.println("Curso: 6to de secundaria");
            System.out.println("Gestion: 2026");
            System.out.println("Matemáticas: 12 preguntas");
            System.out.println("Comprensión lectora: 4 preguntas");
            System.out.println("Razonamiento lógico: 5 preguntas");
            System.out.println("----------------------------------------");
        };
    }
}

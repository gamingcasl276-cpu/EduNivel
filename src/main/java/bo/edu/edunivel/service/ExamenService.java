
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.repository.ExamenRepository;

@Service
public class ExamenService {

    private final ExamenRepository examenRepository;

    public ExamenService(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Transactional
    public void inicializarExamenes() {

        String curso = "6to de secundaria";
        String gestion = "2026";

        List<String> materias = List.of(
                "Matemáticas",
                "Comprensión lectora",
                "Razonamiento lógico",
                "Lenguaje"
        );

        for (String materia : materias) {

            Examen examen = examenRepository
                    .findByAreaIgnoreCaseAndCursoIgnoreCaseAndGestion(
                            materia, curso, gestion
                    )
                    .orElseGet(() -> {
                        Examen nuevo = new Examen();
                        nuevo.setTitulo("Evaluación inicial de " + materia);
                        nuevo.setArea(materia);
                        nuevo.setCurso(curso);
                        nuevo.setGestion(gestion);
                        nuevo.setPuntajeTotal(new BigDecimal("100.00"));
                        nuevo.setActivo(true);
                        return nuevo;
                    });

            examen.setDuracionMinutos(
                    materia.equalsIgnoreCase("Matemáticas") ? 60 : 45
            );

            examenRepository.save(examen);
        }
    }
}


package bo.edu.edunivel.repository;

import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.model.Pregunta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpcionPreguntaRepository
        extends JpaRepository<OpcionPregunta, Long> {

    List<OpcionPregunta> findByPreguntaOrderByOrdenAsc(
            Pregunta pregunta
    );

    List<OpcionPregunta> findByPreguntaIdOrderByOrdenAsc(
            Long preguntaId
    );

    long countByPregunta(Pregunta pregunta);
}

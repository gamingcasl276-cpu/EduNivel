
package bo.edu.edunivel.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.Pregunta;
import bo.edu.edunivel.model.RespuestaExamen;
public interface RespuestaExamenRepository
        extends JpaRepository<RespuestaExamen, Long> {
        
    @EntityGraph(attributePaths = {
    "pregunta",
    "intento",
    "intento.examen",
    "intento.estudiante"
})
List<RespuestaExamen> findByIntentoOrderByIdAsc(
        IntentoExamen intento
);
    Optional<RespuestaExamen> findByIntentoAndPregunta(
            IntentoExamen intento,
            Pregunta pregunta
    );

    long countByIntento(IntentoExamen intento);

    long countByIntentoAndPendienteRevisionTrue(
            IntentoExamen intento
    );
 @EntityGraph(attributePaths = {
    "intento",
    "intento.estudiante",
    "intento.estudiante.usuario",
    "intento.examen",
    "pregunta"
})
List<RespuestaExamen> findByPendienteRevisionTrueOrderByIdAsc();
@EntityGraph(attributePaths = {
    "intento",
    "intento.estudiante",
    "intento.estudiante.usuario",
    "intento.examen",
    "pregunta"
})
Optional<RespuestaExamen> findWithDetailsById(Long id);
}
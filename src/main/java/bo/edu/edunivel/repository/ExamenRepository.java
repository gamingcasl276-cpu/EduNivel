
package bo.edu.edunivel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.edunivel.model.Examen;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByActivoTrue();

    List<Examen> findByCursoIgnoreCaseAndGestionAndActivoTrue(
            String curso,
            String gestion
    );

    Optional<Examen> findByAreaIgnoreCaseAndCursoIgnoreCaseAndGestion(
            String area,
            String curso,
            String gestion
    );

    boolean existsByAreaIgnoreCaseAndCursoIgnoreCaseAndGestion(
            String area,
            String curso,
            String gestion
    );
}

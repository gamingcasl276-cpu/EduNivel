package bo.edu.edunivel.repository;

import bo.edu.edunivel.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}

package bo.edu.edunivel.repository;

import bo.edu.edunivel.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByAreaIgnoreCase(String area);
}

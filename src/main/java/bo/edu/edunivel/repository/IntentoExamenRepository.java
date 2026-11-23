
package bo.edu.edunivel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

import bo.edu.edunivel.model.Estudiante;
import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.model.IntentoExamen;

public interface IntentoExamenRepository
        extends JpaRepository<IntentoExamen, Long> {

    // HISTORIAL DEL ESTUDIANTE CON LOS DATOS DEL EXAMEN.
    @Query("SELECT i FROM IntentoExamen i " +
           "JOIN FETCH i.examen " +
           "WHERE i.estudiante = :estudiante " +
           "ORDER BY i.fechaInicio DESC")
    List<IntentoExamen> buscarHistorialPorEstudiante(
            @Param("estudiante") Estudiante estudiante
    );

    List<IntentoExamen> findByEstudianteOrderByFechaInicioDesc(
            Estudiante estudiante
    );

    List<IntentoExamen> findByEstudianteAndExamen(
            Estudiante estudiante,
            Examen examen
    );

    Optional<IntentoExamen> findByEstudianteAndExamenAndEstado(
            Estudiante estudiante,
            Examen examen,
            String estado
    );

    boolean existsByEstudianteAndExamenAndEstado(
            Estudiante estudiante,
            Examen examen,
            String estado
    );
    // TOTAL DE INTENTOS FINALIZADOS.
long countByEstado(String estado);
long countByEstadoAndExamen(
        String estado,
        Examen examen
);

// TOTAL DE INTENTOS EN CURSO.
long countByEstadoAndPendienteRevisionFalse(String estado);

// TOTAL DE INTENTOS PENDIENTES DE REVISION.
long countByEstadoAndPendienteRevisionTrue(String estado);

// INTENTOS FINALIZADOS POR MATERIA.
long countByEstadoAndExamen_AreaIgnoreCase(
        String estado,
        String area
);

// RESULTADOS FINALIZADOS Y REVISADOS POR MATERIA.
@Query("""
    SELECT i FROM IntentoExamen i
    JOIN FETCH i.examen
    WHERE UPPER(i.estado) = UPPER(:estado)
    AND i.pendienteRevision = false
    AND LOWER(i.examen.area) = LOWER(:area)
    AND i.puntajeObtenido IS NOT NULL
    """)
        List<IntentoExamen> buscarResultadosPorMateria(
        @Param("estado") String estado,
        @Param("area") String area
);

    // RECUPERAR UN INTENTO JUNTO CON SU EXAMEN.
    @Query("SELECT i FROM IntentoExamen i " +
           "JOIN FETCH i.examen " +
           "WHERE i.id = :id " +
           "AND i.estudiante = :estudiante")
    Optional<IntentoExamen> findByIdAndEstudiante(
            @Param("id") Long id,
            @Param("estudiante") Estudiante estudiante
    );

    // LISTAR TODOS LOS INTENTOS DEL CURSO Y GESTIÓN DEL PROYECTO.
    @Query("""
        SELECT i FROM IntentoExamen i
        JOIN FETCH i.examen e
        JOIN FETCH i.estudiante s
        LEFT JOIN FETCH s.usuario u
        WHERE LOWER(e.curso) = LOWER(:curso)
        AND e.gestion = :gestion
        ORDER BY i.fechaInicio DESC
        """)
    List<IntentoExamen> buscarTodosPorCursoYGestion(
            @Param("curso") String curso,
            @Param("gestion") String gestion
    );
        // RECUPERAR UN INTENTO PARA EL DETALLE ADMINISTRATIVO.
    @EntityGraph(attributePaths = {
            "examen",
            "estudiante",
            "estudiante.usuario"
    })
    Optional<IntentoExamen> findWithDetailsById(Long id);
}

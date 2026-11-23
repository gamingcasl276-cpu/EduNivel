
package bo.edu.edunivel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bo.edu.edunivel.model.Rol;
import bo.edu.edunivel.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    @EntityGraph(attributePaths = {"estudiante"})
    Optional<Usuario> findWithEstudianteByNombreUsuario(
            String nombreUsuario
    );

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByRol(Rol rol);

    long countByRol(Rol rol);

    long countByRolAndActivoTrue(Rol rol);

    long countByRolAndActivoFalse(Rol rol);

    @EntityGraph(attributePaths = {"estudiante"})
    List<Usuario> findByRolOrderByIdDesc(Rol rol);
    @EntityGraph(attributePaths = {"estudiante"})
List<Usuario> findByRolAndNombreUsuarioContainingIgnoreCaseOrderByIdDesc(
        Rol rol,
        String nombreUsuario
);
@EntityGraph(attributePaths = {"estudiante"})
@Query("""
    SELECT u FROM Usuario u
    LEFT JOIN u.estudiante e
    WHERE u.rol = :rol
    AND (
        LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :buscar, '%'))
        OR LOWER(e.codigo) LIKE LOWER(CONCAT('%', :buscar, '%'))
    )
    ORDER BY u.id DESC
    """)
List<Usuario> buscarEstudiantesPorUsuarioOCodigo(
        @Param("rol") Rol rol,
        @Param("buscar") String buscar
);
}

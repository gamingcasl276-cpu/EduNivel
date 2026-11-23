
package bo.edu.edunivel.repository;

import bo.edu.edunivel.model.CodigoRegistro;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CodigoRegistroRepository
        extends JpaRepository<CodigoRegistro, Long> {

    Optional<CodigoRegistro> findByCodigoHash(String codigoHash);

    boolean existsByCodigoHash(String codigoHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CodigoRegistro c WHERE c.codigoHash = :hash")
    Optional<CodigoRegistro> buscarPorHashConBloqueo(
            @Param("hash") String hash
    );
}

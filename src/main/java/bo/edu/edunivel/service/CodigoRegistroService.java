
package bo.edu.edunivel.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.CodigoRegistro;
import bo.edu.edunivel.repository.CodigoRegistroRepository;

@Service
public class CodigoRegistroService {

    private final CodigoRegistroRepository codigoRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public CodigoRegistroService(
            CodigoRegistroRepository codigoRepository) {
        this.codigoRepository = codigoRepository;
    }

    @Transactional
    public String generarCodigo() {

        byte[] bytes = new byte[24];
        String codigo;
        String hash;

        do {
            secureRandom.nextBytes(bytes);

            codigo = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes);

            hash = generarHash(codigo);

        } while (codigoRepository.existsByCodigoHash(hash));

        CodigoRegistro registro = new CodigoRegistro();

        registro.setCodigoHash(hash);
        registro.setFechaExpiracion(
                LocalDateTime.now().plusDays(7)
        );
        registro.setUsado(false);
        registro.setActivo(true);

        codigoRepository.save(registro);

        return codigo;
    }

    @Transactional(readOnly = true)
    public boolean validarCodigo(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            return false;
        }

        String hash = generarHash(codigo.trim());

        return codigoRepository.findByCodigoHash(hash)
                .map(registro ->
                        registro.isActivo()
                        && !registro.isUsado()
                        && registro.getFechaExpiracion()
                                .isAfter(LocalDateTime.now())
                )
                .orElse(false);
    }

    @Transactional
    public void consumirCodigo(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes ingresar un código de invitación."
            );
        }

        String hash = generarHash(codigo.trim());

        CodigoRegistro registro = codigoRepository
                .buscarPorHashConBloqueo(hash)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El código de invitación no existe."
                        )
                );

        if (!registro.isActivo()) {
            throw new IllegalArgumentException(
                    "El código de invitación está desactivado."
            );
        }

        if (registro.isUsado()) {
            throw new IllegalArgumentException(
                    "El código de invitación ya fue utilizado."
            );
        }

        if (!registro.getFechaExpiracion()
                .isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "El código de invitación ha expirado."
            );
        }

        registro.setUsado(true);
        registro.setFechaUso(LocalDateTime.now());

        codigoRepository.save(registro);
    }

    private String generarHash(String codigo) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] resultado = digest.digest(
                    codigo.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(resultado);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "No se pudo generar el hash del código.", e
            );
        }
    }
}

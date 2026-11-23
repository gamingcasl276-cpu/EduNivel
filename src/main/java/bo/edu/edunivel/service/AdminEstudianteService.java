
package bo.edu.edunivel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Rol;
import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.UsuarioRepository;

@Service
public class AdminEstudianteService {

    private final UsuarioRepository usuarioRepository;

    public AdminEstudianteService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void cambiarEstado(Long usuarioId, boolean activo) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                    new IllegalArgumentException("Usuario no encontrado.")
                );

        if (usuario.getRol() != Rol.ESTUDIANTE) {
            throw new IllegalArgumentException(
                    "Solo se pueden modificar cuentas de estudiantes."
            );
        }

        usuario.setActivo(activo);

        usuarioRepository.save(usuario);
    }
}

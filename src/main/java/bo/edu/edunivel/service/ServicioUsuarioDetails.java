
package bo.edu.edunivel.service;

import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuarioDetails implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public ServicioUsuarioDetails(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe el usuario: " + nombreUsuario
                ));

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())
                .build();
    }
}

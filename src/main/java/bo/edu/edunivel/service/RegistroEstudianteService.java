
package bo.edu.edunivel.service;

import bo.edu.edunivel.dto.RegistroEstudianteDTO;
import bo.edu.edunivel.model.Estudiante;
import bo.edu.edunivel.model.Rol;
import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.EstudianteRepository;
import bo.edu.edunivel.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodigoRegistroService codigoRegistroService;

    public RegistroEstudianteService(
            EstudianteRepository estudianteRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CodigoRegistroService codigoRegistroService) {

        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.codigoRegistroService = codigoRegistroService;
    }

    @Transactional
    public void registrarEstudiante(RegistroEstudianteDTO dto) {

        String nombreUsuario = dto.getNombreUsuario()
                .trim()
                .toLowerCase();

        String codigoEstudiante = dto.getCodigoEstudiante()
                .trim();

        // Comprobar si el nombre de usuario ya existe
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new IllegalArgumentException(
                    "Ese nombre de usuario ya está registrado."
            );
        }

        // Comprobar si el código estudiantil ya existe
        if (estudianteRepository.existsByCodigo(codigoEstudiante)) {
            throw new IllegalArgumentException(
                    "Ese código de estudiante ya está registrado."
            );
        }

        // Crear el estudiante
        Estudiante estudiante = new Estudiante();

        estudiante.setCodigo(codigoEstudiante);
        estudiante.setCurso(dto.getCurso().trim());
        estudiante.setParalelo(dto.getParalelo());
        estudiante.setGestion(dto.getGestion().trim());

        estudiante = estudianteRepository.save(estudiante);

        // Crear la cuenta de usuario
        Usuario usuario = new Usuario();

        usuario.setNombreUsuario(nombreUsuario);

        // Encriptar la contraseña
        usuario.setContrasena(
                passwordEncoder.encode(dto.getContrasena())
        );

        // El registro público solamente permite estudiantes
        usuario.setRol(Rol.ESTUDIANTE);
        usuario.setActivo(true);
        usuario.setEstudiante(estudiante);

        usuarioRepository.save(usuario);

        // Validar y consumir el código de invitación
        codigoRegistroService.consumirCodigo(
                dto.getCodigoInvitacion().trim()
        );
    }
}

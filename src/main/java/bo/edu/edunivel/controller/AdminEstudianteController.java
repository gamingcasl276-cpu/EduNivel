
package bo.edu.edunivel.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bo.edu.edunivel.model.Rol;
import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.UsuarioRepository;
import bo.edu.edunivel.service.AdminEstudianteService;

@Controller
@RequestMapping("/admin/estudiantes")
public class AdminEstudianteController {

    private final UsuarioRepository usuarioRepository;
    private final AdminEstudianteService estudianteService;

    public AdminEstudianteController(
            UsuarioRepository usuarioRepository,
            AdminEstudianteService estudianteService) {

        this.usuarioRepository = usuarioRepository;
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public String listarEstudiantes(
            @RequestParam(required = false) String buscar,
            Model model) {

        List<Usuario> estudiantes;

        if (buscar != null && !buscar.trim().isEmpty()) {

    estudiantes = usuarioRepository
            .buscarEstudiantesPorUsuarioOCodigo(
                    Rol.ESTUDIANTE,
                    buscar.trim()
            );

            } else {

            estudiantes = usuarioRepository
            .findByRolOrderByIdDesc(Rol.ESTUDIANTE);
            }

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("buscar", buscar);
        model.addAttribute(
        "totalEstudiantes",
        usuarioRepository.countByRol(Rol.ESTUDIANTE)
    );

        model.addAttribute(
        "estudiantesActivos",
        usuarioRepository.countByRolAndActivoTrue(Rol.ESTUDIANTE)
    );

        model.addAttribute(
        "estudiantesInactivos",
        usuarioRepository.countByRolAndActivoFalse(Rol.ESTUDIANTE)
    );

        return "admin/estudiantes";
    }

    @PostMapping("/estado")
    public String cambiarEstado(
            @RequestParam Long usuarioId,
            @RequestParam boolean activo,
            RedirectAttributes redirectAttributes) {

        try {
            estudianteService.cambiarEstado(usuarioId, activo);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    activo
                        ? "Cuenta del estudiante activada correctamente."
                        : "Cuenta del estudiante desactivada correctamente."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/estudiantes";
    }
}

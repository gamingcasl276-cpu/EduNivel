
package bo.edu.edunivel.controller;

import bo.edu.edunivel.dto.RegistroEstudianteDTO;
import bo.edu.edunivel.service.RegistroEstudianteService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    private final RegistroEstudianteService registroService;

    public RegistroController(
            RegistroEstudianteService registroService) {
        this.registroService = registroService;
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {

        model.addAttribute(
                "registroDTO",
                new RegistroEstudianteDTO()
        );

        return "registro";
    }

    // Procesar registro del estudiante
    @PostMapping("/registro")
    public String registrarEstudiante(
            @Valid @ModelAttribute("registroDTO")
            RegistroEstudianteDTO datos,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Comprobar errores de validación
        if (resultado.hasErrors()) {
            datos.setContrasena(null);
            return "registro";
        }

        try {

            registroService.registrarEstudiante(datos);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "¡Tu cuenta fue creada correctamente! Ya puedes iniciar sesión."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            datos.setContrasena(null);

            return "registro";
        }
    }
}

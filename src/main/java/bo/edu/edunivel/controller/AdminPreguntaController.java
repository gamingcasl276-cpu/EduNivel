
package bo.edu.edunivel.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.service.AdminPreguntaService;

@Controller
public class AdminPreguntaController {

    private final AdminPreguntaService adminPreguntaService;
    private final ExamenRepository examenRepository;

    public AdminPreguntaController(
            AdminPreguntaService adminPreguntaService,
            ExamenRepository examenRepository) {

        this.adminPreguntaService = adminPreguntaService;
        this.examenRepository = examenRepository;
    }

    @GetMapping("/admin/preguntas")
    public String mostrarFormulario(Model model) {

        model.addAttribute(
                "examenes",
                examenRepository
                        .findByCursoIgnoreCaseAndGestionAndActivoTrue(
                                "6to de secundaria",
                                "2026"
                        )
        );

        return "admin-preguntas";
    }

    @PostMapping("/admin/preguntas/guardar")
    public String guardarPregunta(
            @RequestParam(required = false) Long examenId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String enunciado,
            @RequestParam(required = false) String opcionA,
            @RequestParam(required = false) String opcionB,
            @RequestParam(required = false) String opcionC,
            @RequestParam(required = false) String opcionD,
            @RequestParam(required = false) String correcta,
            @RequestParam(required = false) String respuestasAceptadas,
            @RequestParam(required = false) BigDecimal puntaje,
            RedirectAttributes redirectAttributes) {

        try {

            adminPreguntaService.guardarPregunta(
                    examenId,
                    tipo,
                    enunciado,
                    opcionA,
                    opcionB,
                    opcionC,
                    opcionD,
                    correcta,
                    respuestasAceptadas,
                    puntaje
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "¡Pregunta guardada correctamente!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/preguntas";
    }
}

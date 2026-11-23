
package bo.edu.edunivel.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.repository.RespuestaExamenRepository;
import bo.edu.edunivel.service.RevisionRespuestaService;

@Controller
@RequestMapping("/admin/revision")
public class AdminRevisionController {

    private final RespuestaExamenRepository respuestaRepository;
    private final RevisionRespuestaService revisionService;

    public AdminRevisionController(
            RespuestaExamenRepository respuestaRepository,
            RevisionRespuestaService revisionService) {

        this.respuestaRepository = respuestaRepository;
        this.revisionService = revisionService;
    }

    // LISTAR RESPUESTAS PENDIENTES.
    @GetMapping
    public String mostrarPendientes(Model model) {

        model.addAttribute(
                "respuestas",
                respuestaRepository.findByPendienteRevisionTrueOrderByIdAsc()
        );

        return "admin/revision-respuestas";
    }

    // MOSTRAR UNA RESPUESTA PARA CALIFICAR.
    @GetMapping("/{respuestaId}")
    public String mostrarRespuesta(
            @PathVariable Long respuestaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        RespuestaExamen respuesta = respuestaRepository
                .findWithDetailsById(respuestaId)
                .orElse(null);

        if (respuesta == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "La respuesta solicitada no existe."
            );
            return "redirect:/admin/revision";
        }

        if (!respuesta.isPendienteRevision()
                || !"FINALIZADO".equals(respuesta.getIntento().getEstado())
                || !"DESARROLLO".equals(respuesta.getPregunta().getTipo())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Esta respuesta ya fue revisada o no requiere calificación manual."
            );

            return "redirect:/admin/revision";
        }

        model.addAttribute("respuesta", respuesta);

        return "admin/calificar-respuesta";
    }

    // GUARDAR CALIFICACION MANUAL.
    @PostMapping("/{respuestaId}/calificar")
    public String calificarRespuesta(
            @PathVariable Long respuestaId,
            @RequestParam BigDecimal puntaje,
            RedirectAttributes redirectAttributes) {

        try {

            revisionService.calificarRespuesta(
                    respuestaId,
                    puntaje,
                    null
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "La respuesta fue calificada correctamente."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/revision";
    }
}


package bo.edu.edunivel.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.service.AdminExamenService;
import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.model.Pregunta;

@Controller
public class AdminExamenController {

    private final AdminExamenService adminExamenService;

    public AdminExamenController(
            AdminExamenService adminExamenService) {

        this.adminExamenService = adminExamenService;
    }

    @GetMapping("/admin/evaluaciones")
    public String mostrarEvaluaciones(Model model) {

        List<Examen> examenes =
                adminExamenService.listarExamenes();

        Map<Long, Long> preguntasPorExamen =
                adminExamenService.contarPreguntasPorExamen();

        Map<Long, Long> intentosPorExamen =
                new LinkedHashMap<>();

        for (Examen examen : examenes) {

            intentosPorExamen.put(
                    examen.getId(),
                    adminExamenService.contarIntentosPorExamen(
                            examen.getId()
                    )
            );
        }

        model.addAttribute("examenes", examenes);
        model.addAttribute(
                "preguntasPorExamen",
                preguntasPorExamen
        );
        model.addAttribute(
                "intentosPorExamen",
                intentosPorExamen
        );

        model.addAttribute(
                "totalExamenes",
                examenes.size()
        );

        model.addAttribute(
                "examenesActivos",
                examenes.stream()
                        .filter(Examen::isActivo)
                        .count()
        );

        model.addAttribute(
                "examenesInactivos",
                examenes.stream()
                        .filter(examen -> !examen.isActivo())
                        .count()
        );

        return "admin-evaluaciones";
     }

    @PostMapping("/admin/evaluaciones/estado")
    public String cambiarEstado(
            @RequestParam Long examenId,
            @RequestParam boolean activo,
            RedirectAttributes redirectAttributes) {

        try {

            adminExamenService.cambiarEstado(
                    examenId,
                    activo
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    activo
                            ? "Evaluación activada correctamente."
                            : "Evaluación desactivada correctamente."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/evaluaciones";
      }

    // MOSTRAR LOS INTENTOS INDIVIDUALES DE LOS ESTUDIANTES.
    @GetMapping("/admin/evaluaciones/intentos")
    public String mostrarIntentos(Model model) {

        model.addAttribute(
                "intentos",
                adminExamenService.listarIntentos()
        );

        model.addAttribute(
                "totalIntentos",
                adminExamenService.listarIntentos().size()
        );

        return "admin-intentos";
    }
     
    @GetMapping("/admin/evaluaciones/intentos/{intentoId}")
    public String mostrarDetalleIntento(
        @PathVariable Long intentoId,
        Model model,
        RedirectAttributes redirectAttributes) {

    try {
        IntentoExamen intento =
                adminExamenService.buscarIntentoDetalle(intentoId);

        List<RespuestaExamen> respuestas =
                adminExamenService.listarRespuestasDelIntento(intentoId);

        List<Pregunta> preguntas =
                adminExamenService.listarPreguntasDelExamen(
                        intento.getExamen().getId()
                );

        Map<Long, RespuestaExamen> respuestaPorPregunta =
                new LinkedHashMap<>();

        for (RespuestaExamen respuesta : respuestas) {
            respuestaPorPregunta.put(
                    respuesta.getPregunta().getId(),
                    respuesta
            );
        }

        Map<Long, List<OpcionPregunta>> opcionesPorPregunta =
                new LinkedHashMap<>();

        for (Pregunta pregunta : preguntas) {
            opcionesPorPregunta.put(
                    pregunta.getId(),
                    adminExamenService.listarOpcionesDePregunta(
                            pregunta.getId()
                    )
            );
        }

        // CALCULAR EL RESUMEN DEL RENDIMIENTO.
        int correctas = 0;
        int incorrectas = 0;
        int parciales = 0;
        int pendientes = 0;
        int sinResponder = 0;

        BigDecimal puntajeTotal = BigDecimal.ZERO;
        BigDecimal puntajeMaximo = BigDecimal.ZERO;

        for (Pregunta pregunta : preguntas) {

            BigDecimal maximo = pregunta.getPuntaje() != null
                    ? pregunta.getPuntaje()
                    : BigDecimal.ZERO;

            puntajeMaximo = puntajeMaximo.add(maximo);

            RespuestaExamen respuesta =
                    respuestaPorPregunta.get(pregunta.getId());

            if (respuesta == null) {
                sinResponder++;
                continue;
            }

            if (respuesta.isPendienteRevision()) {
                pendientes++;
                continue;
            }

            BigDecimal obtenido = respuesta.getPuntajeObtenido() != null
                    ? respuesta.getPuntajeObtenido()
                    : BigDecimal.ZERO;

            puntajeTotal = puntajeTotal.add(obtenido);

            if (maximo.compareTo(BigDecimal.ZERO) > 0
                    && obtenido.compareTo(maximo) >= 0) {

                correctas++;

            } else if (obtenido.compareTo(BigDecimal.ZERO) > 0) {

                parciales++;

            } else {

                incorrectas++;
            }
        }

      BigDecimal porcentaje = BigDecimal.ZERO;

        BigDecimal puntajeExamen = intento.getExamen().getPuntajeTotal();

        if (puntajeExamen != null
        && puntajeExamen.compareTo(BigDecimal.ZERO) > 0) {

            porcentaje = puntajeTotal
            .multiply(BigDecimal.valueOf(100))
            .divide(puntajeExamen, 2, RoundingMode.HALF_UP);
        }
        
        model.addAttribute("intento", intento);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("respuestas", respuestas);
        model.addAttribute("respuestaPorPregunta", respuestaPorPregunta);
        model.addAttribute("opcionesPorPregunta", opcionesPorPregunta);

        model.addAttribute("totalPreguntas", preguntas.size());
        model.addAttribute("totalRespuestas", respuestas.size());

        model.addAttribute("correctas", correctas);
        model.addAttribute("incorrectas", incorrectas);
        model.addAttribute("parciales", parciales);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("sinResponder", sinResponder);

        model.addAttribute("porcentaje", porcentaje);
        model.addAttribute("puntajeCalculado", puntajeTotal);
        model.addAttribute("puntajeMaximo", puntajeMaximo);

        return "admin-detalle-intento";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin/evaluaciones/intentos";
    }
}
}

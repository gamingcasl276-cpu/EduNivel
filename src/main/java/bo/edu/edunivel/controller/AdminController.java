
package bo.edu.edunivel.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bo.edu.edunivel.service.AdminDashboardService;
import bo.edu.edunivel.service.CodigoRegistroService;

@Controller
public class AdminController {

    private final CodigoRegistroService codigoRegistroService;
    private final AdminDashboardService adminDashboardService;

    public AdminController(
            CodigoRegistroService codigoRegistroService,
            AdminDashboardService adminDashboardService) {

        this.codigoRegistroService = codigoRegistroService;
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/admin")
    public String mostrarPanel(Model model) {

        // ESTADÍSTICAS DE ESTUDIANTES.
        model.addAttribute(
                "totalEstudiantes",
                adminDashboardService.contarEstudiantes()
        );

        model.addAttribute(
                "estudiantesActivos",
                adminDashboardService.contarEstudiantesActivos()
        );

        model.addAttribute(
                "estudiantesInactivos",
                adminDashboardService.contarEstudiantesInactivos()
        );

        // ESTADÍSTICAS DE EXÁMENES.
        model.addAttribute(
                "totalExamenes",
                adminDashboardService.contarExamenes()
        );

        model.addAttribute(
                "evaluacionesRealizadas",
                adminDashboardService.contarEvaluacionesRealizadas()
        );

        model.addAttribute(
                "evaluacionesEnCurso",
                adminDashboardService.contarEvaluacionesEnCurso()
        );

        model.addAttribute(
                "evaluacionesPendientesRevision",
                adminDashboardService.contarEvaluacionesPendientesRevision()
        );

        // RESULTADOS POR MATERIA.
        Map<String, Long> resultadosPorMateria =
                adminDashboardService.contarResultadosPorMateria();

        model.addAttribute(
                "resultadosPorMateria",
                resultadosPorMateria
        );

        // PROMEDIOS POR MATERIA.
        Map<String, BigDecimal> promediosPorMateria =
                adminDashboardService.calcularPromediosPorMateria();

        model.addAttribute(
                "promediosPorMateria",
                promediosPorMateria
        );

        // LISTADO DE ESTUDIANTES.
        model.addAttribute(
                "estudiantes",
                adminDashboardService.listarEstudiantes()
        );

        return "admin";
    }

    @PostMapping("/admin/codigos")
    public String generarCodigo(
            RedirectAttributes redirectAttributes) {

        String codigo = codigoRegistroService.generarCodigo();

        redirectAttributes.addFlashAttribute(
                "codigoGenerado", codigo
        );

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Código de invitación generado correctamente."
        );

        return "redirect:/admin";
    }
}

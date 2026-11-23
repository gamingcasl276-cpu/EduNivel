
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.Rol;
import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final String FINALIZADO = "FINALIZADO";
    private static final String EN_CURSO = "EN_CURSO";

    private static final List<String> MATERIAS = List.of(
            "Matemáticas",
            "Comprensión lectora",
            "Razonamiento lógico",
            "Lenguaje"
    );

    private final UsuarioRepository usuarioRepository;
    private final ExamenRepository examenRepository;
    private final IntentoExamenRepository intentoExamenRepository;

    public AdminDashboardService(
            UsuarioRepository usuarioRepository,
            ExamenRepository examenRepository,
            IntentoExamenRepository intentoExamenRepository) {

        this.usuarioRepository = usuarioRepository;
        this.examenRepository = examenRepository;
        this.intentoExamenRepository = intentoExamenRepository;
    }

    public long contarEstudiantes() {
        return usuarioRepository.countByRol(Rol.ESTUDIANTE);
    }

    public long contarEstudiantesActivos() {
        return usuarioRepository.countByRolAndActivoTrue(
                Rol.ESTUDIANTE
        );
    }

    public long contarEstudiantesInactivos() {
        return usuarioRepository.countByRolAndActivoFalse(
                Rol.ESTUDIANTE
        );
    }

    public long contarExamenes() {
        return examenRepository.count();
    }

    // INTENTOS QUE YA FUERON FINALIZADOS.
    public long contarEvaluacionesRealizadas() {
        return intentoExamenRepository.countByEstado(FINALIZADO);
    }

    // INTENTOS QUE TODAVÍA ESTÁN EN CURSO.
    public long contarEvaluacionesEnCurso() {
        return intentoExamenRepository.countByEstado(EN_CURSO);
    }

    // INTENTOS FINALIZADOS QUE NECESITAN REVISIÓN.
    public long contarEvaluacionesPendientesRevision() {
        return intentoExamenRepository
                .countByEstadoAndPendienteRevisionTrue(FINALIZADO);
    }

    // CANTIDAD DE RESULTADOS FINALIZADOS POR MATERIA.
    public Map<String, Long> contarResultadosPorMateria() {

        Map<String, Long> resultados = new LinkedHashMap<>();

        for (String materia : MATERIAS) {

            long cantidad = intentoExamenRepository
                    .countByEstadoAndExamen_AreaIgnoreCase(
                            FINALIZADO,
                            materia
                    );

            resultados.put(materia, cantidad);
        }

        return resultados;
    }

    // PROMEDIO POR MATERIA, EXPRESADO EN PORCENTAJE.
    // SOLO INCLUYE INTENTOS FINALIZADOS Y REVISADOS.
    public Map<String, BigDecimal> calcularPromediosPorMateria() {

        Map<String, BigDecimal> promedios = new LinkedHashMap<>();

        for (String materia : MATERIAS) {

            List<IntentoExamen> resultados =
                    intentoExamenRepository.buscarResultadosPorMateria(
                            FINALIZADO,
                            materia
                    );

            BigDecimal sumaPorcentajes = BigDecimal.ZERO;
            long cantidadValida = 0;

            for (IntentoExamen intento : resultados) {

                BigDecimal puntaje = intento.getPuntajeObtenido();
                BigDecimal puntajeTotal =
                        intento.getExamen().getPuntajeTotal();

                if (puntaje == null
                        || puntajeTotal == null
                        || puntajeTotal.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                BigDecimal porcentaje = puntaje
                        .multiply(new BigDecimal("100"))
                        .divide(puntajeTotal, 2, RoundingMode.HALF_UP);

                sumaPorcentajes = sumaPorcentajes.add(porcentaje);
                cantidadValida++;
            }

            BigDecimal promedio = BigDecimal.ZERO;

            if (cantidadValida > 0) {
                promedio = sumaPorcentajes.divide(
                        BigDecimal.valueOf(cantidadValida),
                        2,
                        RoundingMode.HALF_UP
                );
            }

            promedios.put(materia, promedio);
        }

        return promedios;
    }

    public List<Usuario> listarEstudiantes() {
        return usuarioRepository.findByRolOrderByIdDesc(
                Rol.ESTUDIANTE
        );
    }
}

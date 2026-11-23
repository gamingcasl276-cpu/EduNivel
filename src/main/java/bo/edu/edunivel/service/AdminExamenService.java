
package bo.edu.edunivel.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.PreguntaRepository;
import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.model.Pregunta;
import bo.edu.edunivel.repository.RespuestaExamenRepository;
import bo.edu.edunivel.repository.OpcionPreguntaRepository;

@Service
@Transactional(readOnly = true)
public class AdminExamenService {

    private static final String CURSO = "6to de secundaria";
    private static final String GESTION = "2026";

    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final IntentoExamenRepository intentoRepository;
    private final RespuestaExamenRepository respuestaRepository;
    private final OpcionPreguntaRepository opcionPreguntaRepository;

    public AdminExamenService(
        ExamenRepository examenRepository,
        PreguntaRepository preguntaRepository,
        IntentoExamenRepository intentoRepository,
        RespuestaExamenRepository respuestaRepository,
        OpcionPreguntaRepository opcionPreguntaRepository) {

    this.examenRepository = examenRepository;
    this.preguntaRepository = preguntaRepository;
    this.intentoRepository = intentoRepository;
    this.respuestaRepository = respuestaRepository;
    this.opcionPreguntaRepository = opcionPreguntaRepository;
}

    public List<Examen> listarExamenes() {

        return examenRepository.findAll().stream()
                .filter(examen ->
                        CURSO.equalsIgnoreCase(examen.getCurso())
                        && GESTION.equals(examen.getGestion()))
                .sorted(Comparator.comparing(Examen::getArea))
                .toList();
    }

    public Map<Long, Long> contarPreguntasPorExamen() {

        Map<Long, Long> cantidades = new LinkedHashMap<>();

        for (Examen examen : listarExamenes()) {

            cantidades.put(
                    examen.getId(),
                    preguntaRepository.countByExamen(examen)
            );
        }

        return cantidades;
    }

    public long contarIntentosPorExamen(Long examenId) {

        Examen examen = buscarExamenDelProyecto(examenId);

        return intentoRepository.countByEstadoAndExamen(
                "FINALIZADO",
                examen
        );
    }

    @Transactional
    public void cambiarEstado(Long examenId, boolean activo) {

        Examen examen = buscarExamenDelProyecto(examenId);

        examen.setActivo(activo);

        examenRepository.save(examen);
    }

    private Examen buscarExamenDelProyecto(Long examenId) {

        if (examenId == null) {
            throw new IllegalArgumentException(
                    "No se indicó una evaluación."
            );
        }

        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La evaluación no existe."
                        ));

        if (!CURSO.equalsIgnoreCase(examen.getCurso())
                || !GESTION.equals(examen.getGestion())) {

            throw new IllegalArgumentException(
                    "La evaluación no pertenece a este proyecto."
            );
        }

        return examen;
    }

    // LISTAR TODOS LOS INTENTOS DE LOS ESTUDIANTES.
    public List<bo.edu.edunivel.model.IntentoExamen> listarIntentos() {
        return intentoRepository.buscarTodosPorCursoYGestion(
                CURSO,
                GESTION
        );
    }
        // BUSCAR UN INTENTO PARA MOSTRAR SU DETALLE AL ADMINISTRADOR.
    public IntentoExamen buscarIntentoDetalle(Long intentoId) {

        if (intentoId == null) {
            throw new IllegalArgumentException(
                    "No se indicó el intento que se desea consultar."
            );
        }

        IntentoExamen intento = intentoRepository
                .findWithDetailsById(intentoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El intento solicitado no existe."
                        ));

        if (!CURSO.equalsIgnoreCase(intento.getExamen().getCurso())
                || !GESTION.equals(intento.getExamen().getGestion())) {

            throw new IllegalArgumentException(
                    "El intento no pertenece a este proyecto."
            );
        }

        return intento;
    }

    // LISTAR LAS RESPUESTAS DE UN INTENTO.
    public List<RespuestaExamen> listarRespuestasDelIntento(
            Long intentoId) {

        IntentoExamen intento = buscarIntentoDetalle(intentoId);

        return respuestaRepository.findByIntentoOrderByIdAsc(intento);
    }

    // OBTENER LAS OPCIONES DE UNA PREGUNTA.
    public List<OpcionPregunta> listarOpcionesDePregunta(
            Long preguntaId) {

        return opcionPreguntaRepository
                .findByPreguntaIdOrderByOrdenAsc(preguntaId);
    }
    public List<Pregunta> listarPreguntasDelExamen(Long examenId) {
    Examen examen = examenRepository.findById(examenId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró el examen solicitado."
            ));

    if (!CURSO.equalsIgnoreCase(examen.getCurso())
            || !GESTION.equals(examen.getGestion())) {
        throw new IllegalArgumentException(
                "El examen no pertenece al proyecto EduNivel 2.0."
        );
    }

    return preguntaRepository.findByExamenOrderByOrdenAsc(examen);
}

}

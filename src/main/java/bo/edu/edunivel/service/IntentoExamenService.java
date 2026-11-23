
package bo.edu.edunivel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Estudiante;
import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.PreguntaRepository;

@Service
public class IntentoExamenService {

    private final IntentoExamenRepository intentoRepository;
    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final CalificacionExamenService calificacionExamenService;

    public IntentoExamenService(
            IntentoExamenRepository intentoRepository,
            ExamenRepository examenRepository,
            PreguntaRepository preguntaRepository,
            CalificacionExamenService calificacionExamenService) {

        this.intentoRepository = intentoRepository;
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
        this.calificacionExamenService = calificacionExamenService;
    }

    // INICIAR O RECUPERAR UN INTENTO.
    @Transactional
    public IntentoExamen iniciarExamen(
            Long examenId,
            Estudiante estudiante) {

        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El examen no existe."
                        ));

        if (!examen.isActivo()) {
            throw new IllegalStateException(
                    "Este examen no está disponible."
            );
        }

        if (!examen.getCurso().equalsIgnoreCase(estudiante.getCurso())
                || !examen.getGestion().equals(estudiante.getGestion())) {

            throw new IllegalStateException(
                    "Este examen no corresponde a tu curso o gestión."
            );
        }

        // COMPROBAR QUE EL EXAMEN TENGA PREGUNTAS.
        long cantidadPreguntas =
                preguntaRepository.countByExamen(examen);

        if (cantidadPreguntas == 0) {
            throw new IllegalStateException(
                    "Esta evaluación todavía no tiene preguntas disponibles."
            );
        }

        // BUSCAR LOS INTENTOS ANTERIORES.
        List<IntentoExamen> intentos =
                intentoRepository.findByEstudianteAndExamen(
                        estudiante,
                        examen
                );

        for (IntentoExamen intento : intentos) {

            if ("EN_CURSO".equals(intento.getEstado())) {

                LocalDateTime limite = obtenerFechaLimite(intento);

                if (LocalDateTime.now().isBefore(limite)) {
                    return intento;
                }

                // SI TERMINÓ EL TIEMPO, CALIFICAR Y FINALIZAR.
                calificacionExamenService.finalizarPorTiempo(intento);
            }
        }

        // CREAR UN NUEVO INTENTO.
        IntentoExamen nuevoIntento = new IntentoExamen();

        nuevoIntento.setExamen(examen);
        nuevoIntento.setEstudiante(estudiante);
        nuevoIntento.setFechaInicio(LocalDateTime.now());
        nuevoIntento.setEstado("EN_CURSO");
        nuevoIntento.setPendienteRevision(false);

        return intentoRepository.save(nuevoIntento);
    }

    // OBTENER LA FECHA LÍMITE.
    public LocalDateTime obtenerFechaLimite(IntentoExamen intento) {

        return intento.getFechaInicio().plusMinutes(
                intento.getExamen().getDuracionMinutos()
        );
    }

    // FINALIZAR UN INTENTO POR AGOTAMIENTO DEL TIEMPO.
    @Transactional
    public void finalizarPorTiempo(IntentoExamen intento) {

        calificacionExamenService.finalizarPorTiempo(intento);
    }
}

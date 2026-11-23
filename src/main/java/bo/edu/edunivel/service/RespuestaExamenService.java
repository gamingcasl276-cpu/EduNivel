
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Estudiante;
import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.model.Pregunta;
import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.OpcionPreguntaRepository;
import bo.edu.edunivel.repository.PreguntaRepository;
import bo.edu.edunivel.repository.RespuestaExamenRepository;

@Service
public class RespuestaExamenService {

    private final RespuestaExamenRepository respuestaRepository;
    private final IntentoExamenRepository intentoRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionPreguntaRepository opcionRepository;

    public RespuestaExamenService(
            RespuestaExamenRepository respuestaRepository,
            IntentoExamenRepository intentoRepository,
            PreguntaRepository preguntaRepository,
            OpcionPreguntaRepository opcionRepository) {

        this.respuestaRepository = respuestaRepository;
        this.intentoRepository = intentoRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
    }

    // GUARDAR Y CALIFICAR RESPUESTA DEL ESTUDIANTE.
    @Transactional
    public RespuestaExamen guardarRespuesta(
            Long intentoId,
            Estudiante estudiante,
            Long preguntaId,
            String respuestaTexto,
            Long opcionId) {

        IntentoExamen intento = intentoRepository
                .findByIdAndEstudiante(intentoId, estudiante)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El intento no existe o no te pertenece."
                        ));

        if (!"EN_CURSO".equals(intento.getEstado())) {
            throw new IllegalStateException(
                    "El examen ya no está disponible."
            );
        }

        LocalDateTime limite = intento.getFechaInicio()
                .plusMinutes(intento.getExamen().getDuracionMinutos());

        if (!LocalDateTime.now().isBefore(limite)) {
            throw new IllegalStateException(
                    "El tiempo del examen ha terminado."
            );
        }

        Pregunta pregunta = preguntaRepository.findById(preguntaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La pregunta no existe."
                        ));

        if (!pregunta.getExamen().getId()
                .equals(intento.getExamen().getId())) {

            throw new IllegalArgumentException(
                    "La pregunta no pertenece a este examen."
            );
        }

        RespuestaExamen respuesta = respuestaRepository
                .findByIntentoAndPregunta(intento, pregunta)
                .orElseGet(RespuestaExamen::new);

        respuesta.setIntento(intento);
        respuesta.setPregunta(pregunta);
        respuesta.setRespuestaTexto(respuestaTexto);
        respuesta.setIdOpcionSeleccionada(opcionId);

        String tipo = pregunta.getTipo() == null
                ? ""
                : pregunta.getTipo().toUpperCase().trim();

        respuesta.setPuntajeObtenido(BigDecimal.ZERO);
        respuesta.setPendienteRevision(false);

        switch (tipo) {

            // OPCIÓN MÚLTIPLE Y VERDADERO/FALSO
            case "OPCION_MULTIPLE":
            case "VERDADERO_FALSO":

                if (opcionId == null) {
                    throw new IllegalArgumentException(
                            "Debes seleccionar una opción."
                    );
                }

                OpcionPregunta opcion = opcionRepository.findById(opcionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La opción no existe."
                                ));

                if (opcion.getPregunta() == null
                        || !opcion.getPregunta().getId()
                                .equals(pregunta.getId())) {

                    throw new IllegalArgumentException(
                            "La opción no pertenece a esta pregunta."
                    );
                }

                if (opcion.isEsCorrecta()) {
                    respuesta.setPuntajeObtenido(
                            pregunta.getPuntaje()
                    );
                }

                break;

            // COMPLETAR ESPACIOS: CALIFICACIÓN AUTOMÁTICA
            case "COMPLETAR":

                if (respuestaTexto == null
                        || respuestaTexto.isBlank()) {

                    throw new IllegalArgumentException(
                            "Escribe tu respuesta antes de guardarla."
                    );
                }

                String respuestasCorrectas =
                        pregunta.getRespuestaCorrecta();

                if (respuestasCorrectas == null
                        || respuestasCorrectas.isBlank()) {

                    throw new IllegalStateException(
                            "Esta pregunta todavía no tiene respuestas correctas configuradas."
                    );
                }

                boolean esCorrecta = respuestasCorrectas.lines()
                        .map(String::trim)
                        .filter(valor -> !valor.isEmpty())
                        .anyMatch(valor ->
                                normalizar(valor).equals(
                                        normalizar(respuestaTexto)
                                )
                        );

                if (esCorrecta) {
                    respuesta.setPuntajeObtenido(
                            pregunta.getPuntaje()
                    );
                }

                // Se califica automáticamente.
                respuesta.setPendienteRevision(false);

                break;

            // DESARROLLO: REVISIÓN DEL PROFESOR
            case "DESARROLLO":

                if (respuestaTexto != null
                        && !respuestaTexto.isBlank()) {

                    respuesta.setPendienteRevision(true);
                }

                break;

            default:

                throw new IllegalArgumentException(
                        "El tipo de pregunta no es válido."
                );
        }

        return respuestaRepository.save(respuesta);
    }

    // NORMALIZAR RESPUESTAS PARA COMPARARLAS.
    // Ignora mayúsculas, minúsculas, tildes y espacios repetidos.
    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        String sinTildes = Normalizer.normalize(
                texto,
                Normalizer.Form.NFD
        ).replaceAll("\\p{M}+", "");

        return sinTildes
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    // RECUPERAR TODAS LAS RESPUESTAS DE UN INTENTO.
    @Transactional(readOnly = true)
    public List<RespuestaExamen> obtenerRespuestas(
            IntentoExamen intento) {

        return respuestaRepository.findByIntentoOrderByIdAsc(intento);
    }

    // RECUPERAR LAS RESPUESTAS ORGANIZADAS POR PREGUNTA.
    @Transactional(readOnly = true)
    public Map<Long, RespuestaExamen> obtenerRespuestasPorPregunta(
            IntentoExamen intento) {

        Map<Long, RespuestaExamen> respuestas =
                new LinkedHashMap<>();

        List<RespuestaExamen> lista =
                respuestaRepository.findByIntentoOrderByIdAsc(intento);

        for (RespuestaExamen respuesta : lista) {

            respuestas.put(
                    respuesta.getPregunta().getId(),
                    respuesta
            );
        }

        return respuestas;
    }
}

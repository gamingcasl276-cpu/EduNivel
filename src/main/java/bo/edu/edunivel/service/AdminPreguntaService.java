
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.model.Pregunta;
import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.repository.OpcionPreguntaRepository;
import bo.edu.edunivel.repository.PreguntaRepository;

@Service
public class AdminPreguntaService {

    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionPreguntaRepository opcionRepository;

    public AdminPreguntaService(
            ExamenRepository examenRepository,
            PreguntaRepository preguntaRepository,
            OpcionPreguntaRepository opcionRepository) {

        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
    }

    @Transactional
    public void guardarPregunta(
            Long examenId,
            String tipo,
            String enunciado,
            String opcionA,
            String opcionB,
            String opcionC,
            String opcionD,
            String correcta,
            String respuestasAceptadas,
            BigDecimal puntaje) {

        // Validar materia
        if (examenId == null) {
            throw new IllegalArgumentException(
                    "Selecciona una materia."
            );
        }

        // Validar tipo
        List<String> tiposPermitidos = List.of(
                "OPCION_MULTIPLE",
                "VERDADERO_FALSO",
                "COMPLETAR",
                "DESARROLLO"
        );

        if (tipo == null || !tiposPermitidos.contains(tipo)) {
            throw new IllegalArgumentException(
                    "Selecciona un tipo de pregunta válido."
            );
        }

        // Validar enunciado
        if (enunciado == null || enunciado.isBlank()) {
            throw new IllegalArgumentException(
                    "Escribe el enunciado de la pregunta."
            );
        }

        // Validar puntaje
        if (puntaje == null
                || puntaje.compareTo(BigDecimal.ZERO) <= 0
                || puntaje.compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "El puntaje debe ser mayor que cero y no superar 100."
            );
        }

        // Buscar evaluación
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La evaluación seleccionada no existe."
                        )
                );

        if (!examen.isActivo()
                || examen.getCurso() == null
                || !examen.getCurso().equalsIgnoreCase(
                        "6to de secundaria")
                || examen.getGestion() == null
                || !examen.getGestion().equals("2026")) {

            throw new IllegalArgumentException(
                    "La evaluación no está habilitada para este proyecto."
            );
        }

        // Preparar pregunta
        Pregunta pregunta = new Pregunta();

        pregunta.setExamen(examen);
        pregunta.setArea(examen.getArea());
        pregunta.setTipo(tipo);
        pregunta.setEnunciado(enunciado.trim());
        pregunta.setPuntaje(puntaje);
        pregunta.setOrden(
                (int) preguntaRepository.countByExamen(examen) + 1
        );

        // Preparar alternativas según el tipo
        List<String> alternativas = new ArrayList<>();
        int indiceCorrecto = -1;

        switch (tipo) {

            case "OPCION_MULTIPLE":

                validarCampo(opcionA, "Completa la alternativa A.");
                validarCampo(opcionB, "Completa la alternativa B.");
                validarCampo(opcionC, "Completa la alternativa C.");
                validarCampo(opcionD, "Completa la alternativa D.");

                if (correcta == null
                        || !List.of("A", "B", "C", "D")
                                .contains(correcta)) {

                    throw new IllegalArgumentException(
                            "Selecciona la alternativa correcta."
                    );
                }

                alternativas.add(opcionA.trim());
                alternativas.add(opcionB.trim());
                alternativas.add(opcionC.trim());
                alternativas.add(opcionD.trim());

                indiceCorrecto = "ABCD".indexOf(correcta);

                pregunta.setRespuestaCorrecta(
                        alternativas.get(indiceCorrecto)
                );

                break;

            case "VERDADERO_FALSO":

                if (correcta == null
                        || !List.of("VERDADERO", "FALSO")
                                .contains(correcta)) {

                    throw new IllegalArgumentException(
                            "Selecciona si la respuesta correcta es Verdadero o Falso."
                    );
                }

                alternativas.add("Verdadero");
                alternativas.add("Falso");

                indiceCorrecto = correcta.equals("VERDADERO") ? 0 : 1;

                pregunta.setRespuestaCorrecta(
                        alternativas.get(indiceCorrecto)
                );

                break;

            case "COMPLETAR":

                if (respuestasAceptadas == null
                        || respuestasAceptadas.isBlank()) {

                    throw new IllegalArgumentException(
                            "Escribe al menos una respuesta correcta."
                    );
                }

                List<String> respuestas = respuestasAceptadas.lines()
                        .map(String::trim)
                        .filter(respuesta -> !respuesta.isEmpty())
                        .distinct()
                        .toList();

                if (respuestas.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Escribe al menos una respuesta correcta."
                    );
                }

                pregunta.setRespuestaCorrecta(
                        String.join("\n", respuestas)
                );

                break;

            case "DESARROLLO":

                // La respuesta será revisada por el profesor.
                pregunta.setRespuestaCorrecta(null);

                break;

            default:

                throw new IllegalArgumentException(
                        "Tipo de pregunta no reconocido."
                );
        }

        // Guardar pregunta
        pregunta = preguntaRepository.save(pregunta);

        // Guardar alternativas cuando corresponda
        for (int i = 0; i < alternativas.size(); i++) {

            OpcionPregunta opcion = new OpcionPregunta();

            opcion.setPregunta(pregunta);
            opcion.setTextoOpcion(alternativas.get(i));
            opcion.setOrden(i + 1);
            opcion.setEsCorrecta(i == indiceCorrecto);

            opcionRepository.save(opcion);
        }
    }

    private void validarCampo(String valor, String mensaje) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}

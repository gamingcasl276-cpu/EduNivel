
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.model.Pregunta;
import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.repository.PreguntaRepository;
import bo.edu.edunivel.repository.OpcionPreguntaRepository;

@Service
public class CargaExamenesService {

    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionPreguntaRepository opcionRepository;

    public CargaExamenesService(
            ExamenRepository examenRepository,
            PreguntaRepository preguntaRepository,
            OpcionPreguntaRepository opcionRepository) {

        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
    }

    @Transactional
    public void cargarExamenes() {

        String curso = "6to de secundaria";
        String gestion = "2026";

        Examen matematicas = buscarExamen("Matemáticas", curso, gestion);
        Examen comprension = buscarExamen("Comprensión lectora", curso, gestion);
        Examen logica = buscarExamen("Razonamiento lógico", curso, gestion);

        matematicas.setTitulo("Examen diagnóstico de Matemática");
        matematicas.setDuracionMinutos(60);
        matematicas.setPuntajeTotal(new BigDecimal("100"));
        examenRepository.save(matematicas);

        comprension.setTitulo("Examen de Comprensión lectora");
        comprension.setDuracionMinutos(45);
        comprension.setPuntajeTotal(new BigDecimal("100"));
        examenRepository.save(comprension);

        logica.setTitulo("Examen de Lógica y razonamiento");
        logica.setDuracionMinutos(45);
        logica.setPuntajeTotal(new BigDecimal("100"));
        examenRepository.save(logica);

        cargarMatematicas(matematicas);
        cargarComprension(comprension);
        cargarLogica(logica);
    }

    private Examen buscarExamen(
            String area, String curso, String gestion) {

        return examenRepository
                .findByAreaIgnoreCaseAndCursoIgnoreCaseAndGestion(
                        area, curso, gestion)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el examen de " + area));
    }

    private boolean estaVacio(Examen examen) {
        return preguntaRepository.countByExamen(examen) == 0;
    }

    private void cargarMatematicas(Examen examen) {

        if (!estaVacio(examen)) {
            System.out.println("Matemáticas ya tiene preguntas. Se conserva.");
            return;
        }

        String[] preguntas = {
            "3(2x - 5) + 4 = 2(x + 7) - 6",

            "E = x/(x^2 + 5x + 6) + 15/(x^2 + 9x + 14) - 12/(x^2 + 10x + 21)",

            "Ln(6) - Ln(5 - x) = Ln(x + 2)",

            "5/(√(x^2 + 7) + √(x^2 + 2))",

            "C = √(n&(5^2(n+1/4) · 5^((n-2)/2))/(1/√5 · √(5^n))) - √(n&20^(n+1)/(4^(n+2)+4^(n+1)))",

            "√(log_a(√(n&n) + 1/n log_a(1 + log_a n))&log_a n^n+n)",

            "2^3 + √25 - log_2(8) + 3x = 18",

            "3(x - 2) + 2^3 - √36 = 4 + log_2(16)",

            "√(2x + 9) = x + 3",

            "log_2(x - 1) + log_2(x + 1) = 3",

            "√(√(√(√(√x)))) = √(16&3)",

            "z = n√((2^n · 2^((n+1)/2))/(1/√2 · √(2^n)) - √(n&(2^(n^2+1)+2^(n^2))/3))"
        };

        int orden = 1;

        for (int i = 0; i < preguntas.length; i++) {

            BigDecimal puntos;

            if (i < 3) {
                puntos = new BigDecimal("4");
            } else if (i < 8) {
                puntos = new BigDecimal("8");
            } else {
                puntos = new BigDecimal("12");
            }

            guardarDesarrollo(
                    examen,
                    preguntas[i],
                    puntos,
                    orden++
            );
        }

        System.out.println("Matemáticas: 12 preguntas cargadas.");
    }

    private void cargarComprension(Examen examen) {

        if (!estaVacio(examen)) {
            System.out.println("Comprensión lectora ya tiene preguntas. Se conserva.");
            return;
        }

        guardarOpcionMultiple(
            examen,
            "Lee el texto sobre los espacios verdes urbanos. ¿Cuál es la idea principal del texto?",
            List.of(
                "Los edificios abandonados deberían convertirse en parques.",
                "Los espacios verdes urbanos tienen beneficios, pero requieren mantenimiento y acceso equitativo.",
                "Las ciudades deberían eliminar todos sus estacionamientos.",
                "La biodiversidad solo puede existir en grandes parques."
            ),
            1, 25, 1
        );

        guardarOpcionMultiple(
            examen,
            "Según el texto, ¿cuál de estos NO es mencionado como beneficio de los espacios verdes?",
            List.of(
                "Reducir la temperatura.",
                "Favorecer la biodiversidad.",
                "Ofrecer lugares de descanso.",
                "Aumentar el valor de todas las viviendas de la ciudad por igual."
            ),
            3, 25, 2
        );

        guardarOpcionMultiple(
            examen,
            "¿Qué problema adicional señalan algunos especialistas además del mantenimiento y el acceso?",
            List.of(
                "Que los parques ocupan demasiado espacio.",
                "Que las ciudades tienen demasiada biodiversidad.",
                "Que una mala planificación puede provocar gentrificación verde y desplazar a familias de menores recursos.",
                "Que los habitantes prefieren los estacionamientos."
            ),
            2, 25, 3
        );

        guardarOpcionMultiple(
            examen,
            "Si una ciudad construyera muchos parques solo en los barrios más ricos, sin mantenerlos y sin considerar el impacto en los precios de las viviendas, ¿qué conclusión sería más coherente con el texto?",
            List.of(
                "La ciudad habría solucionado completamente el problema.",
                "Los parques serían beneficiosos aunque nadie pudiera utilizarlos.",
                "La medida cumpliría parcialmente los beneficios, pero fallaría en equidad, sostenibilidad y podría generar desplazamiento de población.",
                "Los parques aumentarían automáticamente la biodiversidad en toda la ciudad."
            ),
            2, 25, 4
        );

        System.out.println("Comprensión lectora: 4 preguntas cargadas.");
    }

    private void cargarLogica(Examen examen) {

        if (!estaVacio(examen)) {
            System.out.println("Razonamiento lógico ya tiene preguntas. Se conserva.");
            return;
        }

        guardarOpcionMultiple(
            examen,
            "Completa la secuencia: 2 - 6 - 12 - 20 - 30 - ¿?",
            List.of("36", "40", "42", "44"),
            2, 20, 1
        );

        guardarOpcionMultiple(
            examen,
            "Todos los estudiantes de 6.º A practican algún deporte. María es estudiante de 6.º A. ¿Cuál afirmación es necesariamente verdadera?",
            List.of(
                "María practica fútbol.",
                "María practica al menos un deporte.",
                "María practica exactamente dos deportes.",
                "Todos los estudiantes practican el mismo deporte."
            ),
            1, 20, 2
        );

        guardarOpcionMultiple(
            examen,
            "Un reloj marca las 3:00. Si la hora avanza exactamente 250 minutos, ¿qué hora marcará?",
            List.of("6:50", "7:00", "7:10", "7:20"),
            2, 20, 3
        );

        guardarOpcionMultiple(
            examen,
            "¿Cuál de estos elementos químicos es esencial en la hemoglobina de la sangre y tiene el símbolo Fe?",
            List.of("Flúor", "Hierro", "Fósforo", "Francio"),
            1, 20, 4
        );

        guardarOpcionMultiple(
            examen,
            "Un padre tiene 42 años y su hijo tiene 14. ¿Dentro de cuántos años el padre tendrá exactamente el doble de la edad de su hijo?",
            List.of("7", "10", "14", "16"),
            2, 20, 5
        );

        System.out.println("Razonamiento lógico: 5 preguntas cargadas.");
        System.out.println("La pregunta de la fila queda pendiente por falta de enunciado.");
    }

    private void guardarDesarrollo(
            Examen examen,
            String enunciado,
            BigDecimal puntaje,
            int orden) {

        Pregunta pregunta = new Pregunta();

        pregunta.setExamen(examen);
        pregunta.setArea(examen.getArea());
        pregunta.setTipo("DESARROLLO");
        pregunta.setEnunciado(enunciado);
        pregunta.setPuntaje(puntaje);
        pregunta.setOrden(orden);
        pregunta.setRespuestaCorrecta(null);

        preguntaRepository.save(pregunta);
    }

    private void guardarOpcionMultiple(
            Examen examen,
            String enunciado,
            List<String> alternativas,
            int indiceCorrecto,
            int puntaje,
            int orden) {

        Pregunta pregunta = new Pregunta();

        pregunta.setExamen(examen);
        pregunta.setArea(examen.getArea());
        pregunta.setTipo("OPCION_MULTIPLE");
        pregunta.setEnunciado(enunciado);
        pregunta.setPuntaje(BigDecimal.valueOf(puntaje));
        pregunta.setOrden(orden);
        pregunta.setRespuestaCorrecta(alternativas.get(indiceCorrecto));

        pregunta = preguntaRepository.save(pregunta);

        for (int i = 0; i < alternativas.size(); i++) {

            OpcionPregunta opcion = new OpcionPregunta();

            opcion.setPregunta(pregunta);
            opcion.setTextoOpcion(alternativas.get(i));
            opcion.setOrden(i + 1);
            opcion.setEsCorrecta(i == indiceCorrecto);

            opcionRepository.save(opcion);
        }
    }
}

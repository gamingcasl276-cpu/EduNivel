
package bo.edu.edunivel.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bo.edu.edunivel.model.Estudiante;
import bo.edu.edunivel.model.Examen;
import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.OpcionPregunta;
import bo.edu.edunivel.model.Pregunta;
import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.ExamenRepository;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.OpcionPreguntaRepository;
import bo.edu.edunivel.repository.PreguntaRepository;
import bo.edu.edunivel.repository.UsuarioRepository;
import bo.edu.edunivel.service.CalificacionExamenService;
import bo.edu.edunivel.service.IntentoExamenService;
import bo.edu.edunivel.service.RespuestaExamenService;

@Controller
public class EstudianteController {

    private final UsuarioRepository usuarioRepository;
    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionPreguntaRepository opcionRepository;
    private final IntentoExamenRepository intentoRepository;

    private final IntentoExamenService intentoService;
    private final RespuestaExamenService respuestaService;
    private final CalificacionExamenService calificacionService;

    public EstudianteController(
            UsuarioRepository usuarioRepository,
            ExamenRepository examenRepository,
            PreguntaRepository preguntaRepository,
            OpcionPreguntaRepository opcionRepository,
            IntentoExamenRepository intentoRepository,
            IntentoExamenService intentoService,
            RespuestaExamenService respuestaService,
            CalificacionExamenService calificacionService) {

        this.usuarioRepository = usuarioRepository;
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
        this.intentoRepository = intentoRepository;
        this.intentoService = intentoService;
        this.respuestaService = respuestaService;
        this.calificacionService = calificacionService;
    }

    // OBTENER EL ESTUDIANTE AUTENTICADO.
    private Estudiante obtenerEstudiante(Authentication authentication) {

        Usuario usuario = usuarioRepository
                .findWithEstudianteByNombreUsuario(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No se encontró la cuenta del estudiante."
                        )
                );

        Estudiante estudiante = usuario.getEstudiante();

        if (estudiante == null) {
            throw new IllegalStateException(
                    "Tu cuenta no tiene un registro de estudiante."
            );
        }

        return estudiante;
    }

   
   
    // PANEL PRINCIPAL DEL ESTUDIANTE.
    @GetMapping("/estudiante")
    public String mostrarPanel(
            Authentication authentication,
            Model model) {

        Usuario usuario = usuarioRepository
                .findWithEstudianteByNombreUsuario(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No se encontró la cuenta del estudiante."
                        )
                );

        Estudiante estudiante = usuario.getEstudiante();

        model.addAttribute("usuario", usuario);
        model.addAttribute("estudiante", estudiante);

        List<Examen> examenes = List.of();

        Map<Long, Long> cantidadPreguntas = new LinkedHashMap<>();

        Map<String, java.math.BigDecimal> progresoPorMateria =
                new LinkedHashMap<>();

        progresoPorMateria.put("Matemáticas", java.math.BigDecimal.ZERO);
        progresoPorMateria.put("Comprensión lectora", java.math.BigDecimal.ZERO);
        progresoPorMateria.put("Razonamiento lógico", java.math.BigDecimal.ZERO);
        progresoPorMateria.put("Lenguaje", java.math.BigDecimal.ZERO);

        List<String> materiasEvaluadas = new ArrayList<>();

        long evaluacionesFinalizadas = 0;
        long evaluacionesEnCurso = 0;
        long resultadosCalificados = 0;

        java.math.BigDecimal sumaPorcentajes =
                java.math.BigDecimal.ZERO;

        java.math.BigDecimal mejorPorcentaje =
                java.math.BigDecimal.ZERO;

        String mejorMateria = "Aún sin resultados";
        boolean hayResultadosCalificados = false;

        if (estudiante != null) {

            examenes = examenRepository
                    .findByCursoIgnoreCaseAndGestionAndActivoTrue(
                            estudiante.getCurso(),
                            estudiante.getGestion()
                    );

            for (Examen examen : examenes) {
                long cantidad = preguntaRepository.countByExamen(examen);
                cantidadPreguntas.put(examen.getId(), cantidad);
            }

            List<IntentoExamen> historial =
                    intentoRepository.buscarHistorialPorEstudiante(estudiante);

            for (IntentoExamen intento : historial) {

                if ("EN_CURSO".equals(intento.getEstado())) {
                    evaluacionesEnCurso++;
                }

                if ("FINALIZADO".equals(intento.getEstado())) {

                    evaluacionesFinalizadas++;

                    if (intento.isPendienteRevision()
                            || intento.getPuntajeObtenido() == null) {
                        continue;
                    }

                    java.math.BigDecimal porcentaje =
                            calificacionService.calcularPorcentaje(intento);

                    String area = intento.getExamen().getArea();

                    // EL HISTORIAL VIENE ORDENADO DEL MÁS RECIENTE
                    // AL MÁS ANTIGUO. SOLO TOMAMOS EL PRIMERO.
                    if (!materiasEvaluadas.contains(area)) {

                        progresoPorMateria.put(area, porcentaje);
                        materiasEvaluadas.add(area);

                        sumaPorcentajes =
                                sumaPorcentajes.add(porcentaje);

                        resultadosCalificados++;

                        if (!hayResultadosCalificados
                                || porcentaje.compareTo(mejorPorcentaje) > 0) {

                            mejorPorcentaje = porcentaje;
                            mejorMateria = area;
                            hayResultadosCalificados = true;
                        }
                    }
                }
            }
        }

        // ANÁLISIS DEL RENDIMIENTO POR MATERIA.
        String materiaMasFuerte = "Aún sin resultados";
        String materiaPorReforzar = "Aún sin resultados";

        java.math.BigDecimal mejorResultadoMateria =
                java.math.BigDecimal.ZERO;

        java.math.BigDecimal menorResultadoMateria =
                new java.math.BigDecimal("100");

        for (String materia : materiasEvaluadas) {

            java.math.BigDecimal resultado =
                    progresoPorMateria.get(materia);

            if (materiaMasFuerte.equals("Aún sin resultados")
                    || resultado.compareTo(mejorResultadoMateria) > 0) {

                mejorResultadoMateria = resultado;
                materiaMasFuerte = materia;
            }

            if (materiaPorReforzar.equals("Aún sin resultados")
                    || resultado.compareTo(menorResultadoMateria) < 0) {

                menorResultadoMateria = resultado;
                materiaPorReforzar = materia;
            }
        }

        // PROMEDIO DE LOS ÚLTIMOS RESULTADOS CALIFICADOS.
        java.math.BigDecimal promedioGeneral =
                java.math.BigDecimal.ZERO;

        if (resultadosCalificados > 0) {

            promedioGeneral = sumaPorcentajes.divide(
                    java.math.BigDecimal.valueOf(resultadosCalificados),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        model.addAttribute("examenes", examenes);
        model.addAttribute("cantidadPreguntas", cantidadPreguntas);

        model.addAttribute("evaluacionesFinalizadas", evaluacionesFinalizadas);
        model.addAttribute("evaluacionesEnCurso", evaluacionesEnCurso);

        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("mejorMateria", mejorMateria);
        model.addAttribute("mejorPorcentaje", mejorPorcentaje);
        model.addAttribute("hayResultadosCalificados", hayResultadosCalificados);

        model.addAttribute("progresoPorMateria", progresoPorMateria);

        model.addAttribute("materiaMasFuerte", materiaMasFuerte);
        model.addAttribute("materiaPorReforzar", materiaPorReforzar);
        model.addAttribute("mejorResultadoMateria", mejorResultadoMateria);
        model.addAttribute("menorResultadoMateria", menorResultadoMateria);
        model.addAttribute("materiasEvaluadas", materiasEvaluadas);

        return "estudiante";
    }

 
   
    
    // PERFIL Y PROGRESO DEL ESTUDIANTE.
    @GetMapping("/estudiante/perfil")
    public String mostrarPerfil(
            Authentication authentication,
            Model model) {

        Usuario usuario = usuarioRepository
                .findWithEstudianteByNombreUsuario(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No se encontró la cuenta del estudiante."
                        )
                );

        Estudiante estudiante = usuario.getEstudiante();

        if (estudiante == null) {
            throw new IllegalStateException(
                    "Tu cuenta no tiene un registro de estudiante."
            );
        }

        // RECUPERAR EL HISTORIAL DEL ESTUDIANTE.
        List<IntentoExamen> historial =
                intentoRepository.buscarHistorialPorEstudiante(estudiante);

        // PROGRESO POR MATERIA.
        Map<String, java.math.BigDecimal> progresoPorMateria =
                new LinkedHashMap<>();

        progresoPorMateria.put("Matemáticas", java.math.BigDecimal.ZERO);
        progresoPorMateria.put("Comprensión lectora", java.math.BigDecimal.ZERO);
        progresoPorMateria.put("Razonamiento lógico", java.math.BigDecimal.ZERO);
        progresoPorMateria.put("Lenguaje", java.math.BigDecimal.ZERO);

        List<String> materiasEvaluadas = new ArrayList<>();

        long evaluacionesFinalizadas = 0;
        long evaluacionesEnCurso = 0;
        long evaluacionesPendientes = 0;
        long resultadosCalificados = 0;

        java.math.BigDecimal sumaPorcentajes =
                java.math.BigDecimal.ZERO;

        // RECORRER EL HISTORIAL.
        // EL REPOSITORIO LO ENTREGA DEL MÁS RECIENTE AL MÁS ANTIGUO.
        for (IntentoExamen intento : historial) {

            if ("EN_CURSO".equals(intento.getEstado())) {
                evaluacionesEnCurso++;
            }

            if ("FINALIZADO".equals(intento.getEstado())) {

                evaluacionesFinalizadas++;

                if (intento.isPendienteRevision()) {
                    evaluacionesPendientes++;
                    continue;
                }

                if (intento.getPuntajeObtenido() == null) {
                    continue;
                }

                String area = intento.getExamen().getArea();

                // TOMAR SOLAMENTE EL ÚLTIMO RESULTADO CALIFICADO
                // DE CADA MATERIA.
                if (!materiasEvaluadas.contains(area)) {

                    java.math.BigDecimal porcentaje =
                            calificacionService.calcularPorcentaje(intento);

                    progresoPorMateria.put(area, porcentaje);
                    materiasEvaluadas.add(area);

                    sumaPorcentajes =
                            sumaPorcentajes.add(porcentaje);

                    resultadosCalificados++;
                }
            }
        }

        // CALCULAR EL PROMEDIO DE LOS ÚLTIMOS RESULTADOS
        // CALIFICADOS DE CADA MATERIA.
        java.math.BigDecimal promedioGeneral =
                java.math.BigDecimal.ZERO;

        if (resultadosCalificados > 0) {

            promedioGeneral = sumaPorcentajes.divide(
                    java.math.BigDecimal.valueOf(resultadosCalificados),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        // ENVIAR LOS DATOS A LA VISTA.
        model.addAttribute("usuario", usuario);
        model.addAttribute("estudiante", estudiante);

        model.addAttribute("evaluacionesFinalizadas", evaluacionesFinalizadas);
        model.addAttribute("evaluacionesEnCurso", evaluacionesEnCurso);
        model.addAttribute("evaluacionesPendientes", evaluacionesPendientes);

        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("resultadosCalificados", resultadosCalificados);

        model.addAttribute("progresoPorMateria", progresoPorMateria);
        model.addAttribute("materiasEvaluadas", materiasEvaluadas);

        return "perfil-estudiante";
    }

    // HISTORIAL DE EVALUACIONES DEL ESTUDIANTE.
    @GetMapping("/estudiante/historial")
    public String mostrarHistorial(
            Authentication authentication,
            Model model) {

        Estudiante estudiante = obtenerEstudiante(authentication);

        List<IntentoExamen> historial =
                intentoRepository.buscarHistorialPorEstudiante(estudiante);

        Map<Long, java.math.BigDecimal> porcentajes =
                new LinkedHashMap<>();

        for (IntentoExamen intento : historial) {

    if ("FINALIZADO".equals(intento.getEstado())
            && !intento.isPendienteRevision()
            && intento.getPuntajeObtenido() != null) {

        porcentajes.put(
                intento.getId(),
                calificacionService.calcularPorcentaje(intento)
        );
    }
}

        model.addAttribute("historial", historial);
        model.addAttribute("porcentajes", porcentajes);
        model.addAttribute("estudiante", estudiante);

        return "historial-examenes";
    }



    // INICIAR O RECUPERAR UN INTENTO.
    @GetMapping("/estudiante/examen/{examenId}")
    public String iniciarExamen(
            @PathVariable Long examenId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Estudiante estudiante = obtenerEstudiante(authentication);

            IntentoExamen intento =
                    intentoService.iniciarExamen(examenId, estudiante);

            return "redirect:/estudiante/examen/"
                    + intento.getId() + "/resolver";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            return "redirect:/estudiante";
        }
    }

    // MOSTRAR LAS PREGUNTAS DEL EXAMEN.
    @GetMapping("/estudiante/examen/{intentoId}/resolver")
    public String resolverExamen(
            @PathVariable Long intentoId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            Estudiante estudiante = obtenerEstudiante(authentication);

            IntentoExamen intento = intentoRepository
                    .findByIdAndEstudiante(intentoId, estudiante)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El intento no existe o no te pertenece."
                            )
                    );

            if (!"EN_CURSO".equals(intento.getEstado())) {

                return "redirect:/estudiante/examen/"
                        + intento.getId() + "/resultado";
            }

            LocalDateTime fechaLimite =
                    intentoService.obtenerFechaLimite(intento);

            if (!LocalDateTime.now().isBefore(fechaLimite)) {

                calificacionService.finalizarExamen(
                        intento.getId(),
                        estudiante
                );

                return "redirect:/estudiante/examen/"
                        + intento.getId() + "/resultado";
            }

            List<Pregunta> preguntas =
                    preguntaRepository.findByExamenOrderByOrdenAsc(
                            intento.getExamen()
                    );

            // RECUPERAR LAS RESPUESTAS GUARDADAS.
            Map<Long, RespuestaExamen> respuestasGuardadas =
                    respuestaService.obtenerRespuestasPorPregunta(intento);

            List<Map<String, Object>> preguntasVista =
                    new ArrayList<>();

            for (Pregunta pregunta : preguntas) {

                Map<String, Object> preguntaVista =
                        new LinkedHashMap<>();

                preguntaVista.put("id", pregunta.getId());
                preguntaVista.put("enunciado", pregunta.getEnunciado());
                preguntaVista.put("tipo", pregunta.getTipo());

                // RECUPERAR LA RESPUESTA GUARDADA.
                RespuestaExamen respuestaGuardada =
                        respuestasGuardadas.get(pregunta.getId());

                preguntaVista.put(
                        "opcionSeleccionadaId",
                        respuestaGuardada != null
                                ? respuestaGuardada.getIdOpcionSeleccionada()
                                : null
                );

                preguntaVista.put(
                        "respuestaTexto",
                        respuestaGuardada != null
                                ? respuestaGuardada.getRespuestaTexto()
                                : ""
                );

                // RECUPERAR LAS OPCIONES.
                List<Map<String, Object>> opcionesVista =
                        new ArrayList<>();

                List<OpcionPregunta> opciones =
                        opcionRepository.findByPreguntaOrderByOrdenAsc(
                                pregunta
                        );

                for (OpcionPregunta opcion : opciones) {

                    Map<String, Object> opcionVista =
                            new LinkedHashMap<>();

                    opcionVista.put("id", opcion.getId());
                    opcionVista.put(
                            "textoOpcion",
                            opcion.getTextoOpcion()
                    );

                    // NO ENVIAR AL HTML SI LA OPCIÓN ES CORRECTA.
                    opcionesVista.add(opcionVista);
                }

                preguntaVista.put("opciones", opcionesVista);

                preguntasVista.add(preguntaVista);
            }

            long fechaLimiteEpoch = fechaLimite
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            model.addAttribute("intento", intento);
            model.addAttribute("examen", intento.getExamen());
            model.addAttribute("preguntas", preguntasVista);
            model.addAttribute("fechaLimiteEpoch", fechaLimiteEpoch);

            return "resolver-examen";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            return "redirect:/estudiante";
        }
    }

    // GUARDAR UNA RESPUESTA.
    @PostMapping("/estudiante/examen/{intentoId}/respuesta")
    public String guardarRespuesta(
            @PathVariable Long intentoId,
            @RequestParam Long preguntaId,
            @RequestParam(required = false) Long opcionId,
            @RequestParam(required = false) String respuestaTexto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Estudiante estudiante = obtenerEstudiante(authentication);

            respuestaService.guardarRespuesta(
                    intentoId,
                    estudiante,
                    preguntaId,
                    respuestaTexto,
                    opcionId
            );

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Respuesta guardada correctamente."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );
        }

        return "redirect:/estudiante/examen/"
                + intentoId + "/resolver";
    }

    // FINALIZAR LA EVALUACIÓN.
    @PostMapping("/estudiante/examen/{intentoId}/finalizar")
    public String finalizarExamen(
            @PathVariable Long intentoId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Estudiante estudiante = obtenerEstudiante(authentication);

            calificacionService.finalizarExamen(
                    intentoId,
                    estudiante
            );

            return "redirect:/estudiante/examen/"
                    + intentoId + "/resultado";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            return "redirect:/estudiante";
        }
    }

    // MOSTRAR EL RESULTADO DEL EXAMEN.
    @GetMapping("/estudiante/examen/{intentoId}/resultado")
    public String mostrarResultado(
            @PathVariable Long intentoId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            Estudiante estudiante = obtenerEstudiante(authentication);

            IntentoExamen intento = intentoRepository
                    .findByIdAndEstudiante(intentoId, estudiante)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El intento no existe o no te pertenece."
                            )
                    );

            if ("EN_CURSO".equals(intento.getEstado())) {

                return "redirect:/estudiante/examen/"
                        + intento.getId() + "/resolver";
            }

            model.addAttribute("intento", intento);

                if (!intento.isPendienteRevision()
                  && intento.getPuntajeObtenido() != null) {

         model.addAttribute(
            "porcentaje",
            calificacionService.calcularPorcentaje(intento)
         );
        }

                return "resultado-examen";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            return "redirect:/estudiante";
        }
    }
}

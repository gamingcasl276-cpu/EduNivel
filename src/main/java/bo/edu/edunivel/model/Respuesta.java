package bo.edu.edunivel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "respuesta",
       uniqueConstraints = @UniqueConstraint(name = "uk_respuesta_estudiante_pregunta",
                                             columnNames = {"id_estudiante", "id_pregunta"}))
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta pregunta;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "puntaje_obtenido", precision = 5, scale = 2)
    private BigDecimal puntajeObtenido;

    public Long getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public BigDecimal getPuntajeObtenido() { return puntajeObtenido; }
    public void setPuntajeObtenido(BigDecimal puntajeObtenido) { this.puntajeObtenido = puntajeObtenido; }
}

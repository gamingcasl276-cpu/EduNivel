
package bo.edu.edunivel.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "respuesta_examen",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_respuesta_examen_intento_pregunta",
            columnNames = {"id_intento", "id_pregunta"}
        )
    }
)
public class RespuestaExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta_examen")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_intento", nullable = false)
    private IntentoExamen intento;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta pregunta;

    @Column(name = "respuesta_texto", columnDefinition = "TEXT")
    private String respuestaTexto;

    @Column(name = "id_opcion_seleccionada")
    private Long idOpcionSeleccionada;

    @Column(name = "puntaje_obtenido", precision = 6, scale = 2)
    private BigDecimal puntajeObtenido;

    @Column(name = "pendiente_revision", nullable = false)
    private boolean pendienteRevision = false;

    public Long getId() {
        return id;
    }

    public IntentoExamen getIntento() {
        return intento;
    }

    public void setIntento(IntentoExamen intento) {
        this.intento = intento;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuestaTexto() {
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto) {
        this.respuestaTexto = respuestaTexto;
    }

    public Long getIdOpcionSeleccionada() {
        return idOpcionSeleccionada;
    }

    public void setIdOpcionSeleccionada(Long idOpcionSeleccionada) {
        this.idOpcionSeleccionada = idOpcionSeleccionada;
    }

    public BigDecimal getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(BigDecimal puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public boolean isPendienteRevision() {
        return pendienteRevision;
    }

    public void setPendienteRevision(boolean pendienteRevision) {
        this.pendienteRevision = pendienteRevision;
    }
}

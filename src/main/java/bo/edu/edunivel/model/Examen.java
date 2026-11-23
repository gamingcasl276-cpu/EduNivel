
package bo.edu.edunivel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "examen",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_examen_area_curso_gestion",
            columnNames = {"area", "curso", "gestion"}
        )
    }
)
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen")
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 40)
    private String area;

    @Column(nullable = false, length = 40)
    private String curso;

    @Column(nullable = false, length = 10)
    private String gestion;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos = 45;

    @Column(name = "puntaje_total", nullable = false,
            precision = 6, scale = 2)
    private BigDecimal puntajeTotal = new BigDecimal("100.00");

    @Column(nullable = false)
    private boolean activo = true;

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getGestion() {
        return gestion;
    }

    public void setGestion(String gestion) {
        this.gestion = gestion;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public BigDecimal getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(BigDecimal puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

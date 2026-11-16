package bo.edu.edunivel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "encuesta")
public class Encuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encuesta")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_estudiante", nullable = false, unique = true)
    private Estudiante estudiante;

    @Column(name = "horas_estudio")
    private Integer horasEstudio;

    @Column(name = "acceso_internet", length = 60)
    private String accesoInternet;

    @Column(name = "acceso_computadora", length = 60)
    private String accesoComputadora;

    @Column(name = "uso_herramientas_digitales", length = 60)
    private String usoHerramientasDigitales;

    @Column(name = "metodo_estudio", length = 100)
    private String metodoEstudio;

    @Column(name = "resolucion_dudas", length = 100)
    private String resolucionDudas;

    @Column(name = "area_refuerzo", length = 60)
    private String areaRefuerzo;

    @Column(name = "intencion_estudios_superiores", length = 100)
    private String intencionEstudiosSuperiores;

    @Column(name = "autopercepcion_preparacion", length = 60)
    private String autopercepcionPreparacion;

    @Column(name = "principal_desafio", length = 100)
    private String principalDesafio;

    public Long getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public Integer getHorasEstudio() { return horasEstudio; }
    public void setHorasEstudio(Integer horasEstudio) { this.horasEstudio = horasEstudio; }
    public String getAccesoInternet() { return accesoInternet; }
    public void setAccesoInternet(String accesoInternet) { this.accesoInternet = accesoInternet; }
    public String getAccesoComputadora() { return accesoComputadora; }
    public void setAccesoComputadora(String accesoComputadora) { this.accesoComputadora = accesoComputadora; }
    public String getUsoHerramientasDigitales() { return usoHerramientasDigitales; }
    public void setUsoHerramientasDigitales(String usoHerramientasDigitales) { this.usoHerramientasDigitales = usoHerramientasDigitales; }
    public String getMetodoEstudio() { return metodoEstudio; }
    public void setMetodoEstudio(String metodoEstudio) { this.metodoEstudio = metodoEstudio; }
    public String getResolucionDudas() { return resolucionDudas; }
    public void setResolucionDudas(String resolucionDudas) { this.resolucionDudas = resolucionDudas; }
    public String getAreaRefuerzo() { return areaRefuerzo; }
    public void setAreaRefuerzo(String areaRefuerzo) { this.areaRefuerzo = areaRefuerzo; }
    public String getIntencionEstudiosSuperiores() { return intencionEstudiosSuperiores; }
    public void setIntencionEstudiosSuperiores(String intencionEstudiosSuperiores) { this.intencionEstudiosSuperiores = intencionEstudiosSuperiores; }
    public String getAutopercepcionPreparacion() { return autopercepcionPreparacion; }
    public void setAutopercepcionPreparacion(String autopercepcionPreparacion) { this.autopercepcionPreparacion = autopercepcionPreparacion; }
    public String getPrincipalDesafio() { return principalDesafio; }
    public void setPrincipalDesafio(String principalDesafio) { this.principalDesafio = principalDesafio; }
}

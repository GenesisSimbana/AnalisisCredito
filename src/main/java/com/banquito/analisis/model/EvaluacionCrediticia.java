package com.banquito.analisis.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluacion_crediticia", schema = "analisis_crediticio")
@Getter @Setter @NoArgsConstructor @ToString
public class EvaluacionCrediticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer idEvaluacion;

    @Column(name = "id_solicitud", nullable = false, unique = true)
    private Integer idSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_consulta")
    private ConsultaBuro consultaBuro;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;

    @Column(name = "score_interno", precision = 6, scale = 2)
    private BigDecimal scoreInterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_automatico", nullable = false)
    private AnalisisEnums.EvaluacionResultado resultadoAutomatico;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_riesgo")
    private AnalisisEnums.CategoriaRiesgo categoriaRiesgo;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_final")
    private AnalisisEnums.DecisionFinal decisionFinal;

    @Column(name = "justificacion_final", length = 255)
    private String justificacionFinal;

    @Column(name = "version", nullable = false)
    private Integer version;
} 
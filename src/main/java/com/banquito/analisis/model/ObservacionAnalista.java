package com.banquito.analisis.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "observacion_analista", schema = "analisis_crediticio")
@Getter @Setter @NoArgsConstructor @ToString
public class ObservacionAnalista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_observacion")
    private Integer idObservacion;

    @ManyToOne
    @JoinColumn(name = "id_evaluacion")
    private EvaluacionCrediticia evaluacionCrediticia;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_analista", nullable = false)
    private AnalisisEnums.DecisionAutoEnum decisionAnalista;

    @Column(name = "justificacion", nullable = false, length = 255)
    private String justificacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "version", nullable = false)
    private Integer version;
} 
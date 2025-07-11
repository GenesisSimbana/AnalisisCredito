package com.banquito.analisis.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultas_buro", schema = "analisis_crediticio")
@Getter @Setter @NoArgsConstructor @ToString
public class ConsultaBuro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Integer idConsulta;

    @Column(name = "id_solicitud", nullable = false)
    private Integer idSolicitud;

    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_consulta", nullable = false)
    private AnalisisEnums.EstadoConsultaBuroEnum estadoConsulta;

    @Column(name = "fuente_buro")
    private String fuenteBuro;

    @Column(name = "score_externo", nullable = false, precision = 6, scale = 2)
    private BigDecimal scoreExterno;

    @Column(name = "cuentas_activas", nullable = false)
    private Integer cuentasActivas;

    @Column(name = "cuentas_morosas", nullable = false)
    private Integer cuentasMorosas;

    @Column(name = "monto_total_adeudado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotalAdeudado;

    @Column(name = "monto_moroso_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoMorosoTotal;

    @Column(name = "dias_mora_promedio", nullable = false, precision = 3, scale = 2)
    private BigDecimal diasMoraPromedio;

    @Column(name = "fecha_primera_mora", nullable = false)
    private LocalDate fechaPrimeraMora;

    @Column(name = "capacidad_pago")
    private BigDecimal capacidadPago;

    @Column(name = "version", nullable = false)
    private Integer version;
} 
package com.banquito.analisis.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreditEvaluationDTO {
    private Integer idSolicitud;
    private String estado; // Resultado automático (APROBADO, RECHAZADO, EN_REVISION)
    private BigDecimal capacidadPago;
    private String nivelRiesgo; // BAJO, MEDIO, ALTO
    private String decisionAutomatica; // APROBADO, RECHAZADO, EN_REVISION
    private String observaciones;
    private String justificacionAnalista;
} 
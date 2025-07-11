package com.banquito.analisis.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SolicitudOriginacionDTO {
    private String idSolicitud;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal cuotaMensual;
    private String estado;
    // Puedes agregar más campos si Originación los expone
} 
package com.banquito.analisis.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolicitudOriginacionDTO {
    private String idSolicitud;
    private String cedula;
    private String estado;
} 


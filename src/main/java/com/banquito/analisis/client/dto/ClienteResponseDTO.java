package com.banquito.analisis.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private String idClienteCore;
    private String cedula;
    private String nombres;
    private String genero;
    private LocalDateTime fechaNacimiento;
    private String nivelEstudio;
    private String estadoCivil;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private String actividadEconomica;
    private String estado; 
    private String correoTransaccional;
    private String telefonoTransaccional;
    private String telefonoTipo;
    private String telefonoNumero;
    private String direccionTipo;
    private String direccionLinea1;
    private String direccionLinea2;
    private String direccionCodigoPostal;
    private String direccionGeoCodigo;
    private String mensaje;
    private Boolean existeEnCore;
    private Boolean esCliente;
    private BigDecimal scoreInterno;
} 

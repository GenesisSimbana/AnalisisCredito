package com.banquito.analisis.client;

import com.banquito.analisis.client.dto.ClienteResponseDTO;
import com.banquito.analisis.client.dto.SolicitudOriginacionDTO;
import com.banquito.analisis.client.dto.SolicitudResumenDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@Profile("demo")

public class OriginacionClientMock implements OriginacionClient {
    private final Random random = new Random();

    @Override
    public SolicitudOriginacionDTO obtenerSolicitud(String idSolicitud) {
        SolicitudOriginacionDTO dto = new SolicitudOriginacionDTO();
        dto.setIdSolicitud(idSolicitud);
        dto.setCedula("1234567" + idSolicitud); // Cedula única por solicitud
        dto.setEstado("PENDIENTE_EVALUACION");
        return dto;
    }

    @Override
    public SolicitudResumenDTO obtenerResumen(Integer idSolicitud) {
        SolicitudResumenDTO resumen = new SolicitudResumenDTO();
        resumen.setId_solicitud(Long.valueOf(idSolicitud));
        resumen.setPrecio_final_vehiculo(new BigDecimal("20000"));
        resumen.setMonto_aprobado(new BigDecimal("15000"));
        resumen.setPlazo_final_meses(36);
        resumen.setTasa_efectiva_anual(new BigDecimal("0.12"));
        return resumen;
    }

    @Override
    public ClienteResponseDTO obtenerClientePorCedula(String cedula) {
        ClienteResponseDTO cliente = new ClienteResponseDTO();
        cliente.setCedula(cedula);
        cliente.setScoreInterno(new BigDecimal(600 + random.nextInt(200))); // 600-799
        cliente.setIngresos(new BigDecimal(1000 + random.nextInt(2000)));
        cliente.setEgresos(new BigDecimal(300 + random.nextInt(700)));
        cliente.setNombres("Cliente " + cedula);
        cliente.setEstado("ACTIVO");
        // ...otros campos simulados si los necesitas
        return cliente;
    }
} 
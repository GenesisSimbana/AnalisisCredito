package com.banquito.analisis.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.banquito.analisis.client.dto.SolicitudOriginacionDTO;
import com.banquito.analisis.client.dto.SolicitudResumenDTO;
import com.banquito.analisis.client.dto.ClienteResponseDTO;

@FeignClient(name = "originacion", url = "${originacion.url=http://ec2-3-15-235-240.us-east-2.compute.amazonaws.com:8081}")
public interface OriginacionClient {
    @GetMapping("/api/v1/solicitudes/{idSolicitud}")
    SolicitudOriginacionDTO obtenerSolicitud(@PathVariable("idSolicitud") String idSolicitud);

    @GetMapping("/api/v1/solicitudes/{idSolicitud}/resumen")
    SolicitudResumenDTO obtenerResumen(@PathVariable("idSolicitud") Integer idSolicitud);

    @GetMapping("/api/v1/clientes/{cedula}")
    ClienteResponseDTO obtenerClientePorCedula(@PathVariable("cedula") String cedula);
} 
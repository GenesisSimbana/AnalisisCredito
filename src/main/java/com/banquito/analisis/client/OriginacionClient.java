package com.banquito.analisis.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.banquito.analisis.client.dto.SolicitudOriginacionDTO;

@FeignClient(name = "originacion", url = "${originacion.url:http://18.223.158.69:8081}")
public interface OriginacionClient {
    @GetMapping("/api/v1/solicitudes/{idSolicitud}")
    SolicitudOriginacionDTO obtenerSolicitud(@PathVariable("idSolicitud") String idSolicitud);
} 
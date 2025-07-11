package com.banquito.analisis.controller;

import com.banquito.analisis.controller.dto.CreditEvaluationDTO;
import com.banquito.analisis.controller.mapper.CreditEvaluationMapper;
import com.banquito.analisis.service.AnalisisCrediticioService;
import com.banquito.analisis.model.EvaluacionCrediticia;
import com.banquito.analisis.model.AnalisisEnums;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/credit-analysis")
@RequiredArgsConstructor
@Tag(name = "Motor de Análisis Crediticio", description = "API para evaluación de solicitudes de crédito automotriz")
public class CreditAnalysisController {

    private final AnalisisCrediticioService analisisCrediticioService;
    private final CreditEvaluationMapper creditEvaluationMapper;

    // Módulos 1, 2 y 3: Evaluación automática de la solicitud
    @PostMapping("/{idSolicitud}/evaluate")
    @Operation(summary = "Ejecutar evaluación automática de la solicitud")
    public ResponseEntity<CreditEvaluationDTO> evaluarSolicitud(@PathVariable Integer idSolicitud) {
        EvaluacionCrediticia evaluacion = analisisCrediticioService.evaluarSolicitud(idSolicitud);
        CreditEvaluationDTO dto = creditEvaluationMapper.toDTO(evaluacion);
        return ResponseEntity.ok(dto);
    }

    // Módulo 4: Revisión del analista
    @PatchMapping("/{idSolicitud}/analyst-review")
    @Operation(summary = "Registrar revisión/decisión del analista")
    public ResponseEntity<Void> registrarRevisionAnalista(
            @PathVariable Integer idSolicitud,
            @RequestParam AnalisisEnums.DecisionAutoEnum decision,
            @RequestParam String justificacion) {
        analisisCrediticioService.registrarRevisionAnalista(idSolicitud, decision, justificacion);
        return ResponseEntity.ok().build();
    }

    // Consultar resultado de evaluación crediticia
    @GetMapping("/{idSolicitud}")
    @Operation(summary = "Consultar resultado de evaluación crediticia")
    public ResponseEntity<CreditEvaluationDTO> obtenerEvaluacion(@PathVariable Integer idSolicitud) {
        EvaluacionCrediticia evaluacion = analisisCrediticioService.obtenerEvaluacionPorSolicitud(idSolicitud);
        CreditEvaluationDTO dto = creditEvaluationMapper.toDTO(evaluacion);
        return ResponseEntity.ok(dto);
    }

    // Listar todas las evaluaciones crediticias
    @GetMapping
    @Operation(summary = "Listar todas las evaluaciones crediticias")
    public ResponseEntity<List<CreditEvaluationDTO>> listarEvaluaciones() {
        List<EvaluacionCrediticia> evaluaciones = analisisCrediticioService.listarEvaluaciones();
        List<CreditEvaluationDTO> dtos = evaluaciones.stream().map(creditEvaluationMapper::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }
} 
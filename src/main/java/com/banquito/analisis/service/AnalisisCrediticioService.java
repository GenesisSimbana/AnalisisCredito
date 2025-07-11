package com.banquito.analisis.service;

import com.banquito.analisis.client.OriginacionClient;
import com.banquito.analisis.client.dto.SolicitudOriginacionDTO;
import com.banquito.analisis.model.*;
import com.banquito.analisis.repository.ConsultaBuroRepository;
import com.banquito.analisis.repository.EvaluacionCrediticiaRepository;
import com.banquito.analisis.repository.ObservacionAnalistaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalisisCrediticioService {

    private final OriginacionClient originacionClient;
    private final ConsultaBuroRepository consultaBuroRepository;
    private final EvaluacionCrediticiaRepository evaluacionCrediticiaRepository;
    private final ObservacionAnalistaRepository observacionAnalistaRepository;

    /**
     * Módulos 1, 2 y 3: Evaluación automática de la solicitud
     */
    @Transactional
    public EvaluacionCrediticia evaluarSolicitud(Integer idSolicitud) {
        // 1. Obtener datos de Originación
        SolicitudOriginacionDTO solicitud = originacionClient.obtenerSolicitud(idSolicitud.toString());
        if (!"PENDIENTE_EVALUACION".equalsIgnoreCase(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se puede evaluar solicitudes pendientes");
        }

        // 2. Consultar buró externo (simulado aquí, reemplaza por integración real si la tienes)
        ConsultaBuro consultaBuro = new ConsultaBuro();
        consultaBuro.setIdSolicitud(idSolicitud);
        consultaBuro.setFechaConsulta(LocalDateTime.now());
        consultaBuro.setEstadoConsulta(AnalisisEnums.EstadoConsultaBuroEnum.EXITOSA);
        consultaBuro.setFuenteBuro("EXTERNO");
        consultaBuro.setScoreExterno(new BigDecimal("800"));
        consultaBuro.setCuentasActivas(3);
        consultaBuro.setCuentasMorosas(0);
        consultaBuro.setMontoTotalAdeudado(new BigDecimal("15000"));
        consultaBuro.setMontoMorosoTotal(BigDecimal.ZERO);
        consultaBuro.setDiasMoraPromedio(new BigDecimal("0"));
        consultaBuro.setFechaPrimeraMora(LocalDate.now());
        consultaBuro.setCapacidadPago(null); // Se calcula en la evaluación
        consultaBuro.setVersion(1);
        consultaBuro = consultaBuroRepository.save(consultaBuro);

        // 3. Evaluación interna
        BigDecimal ingresoNeto = solicitud.getIngresos().subtract(solicitud.getEgresos());
        BigDecimal capacidadPago = ingresoNeto.multiply(new BigDecimal("0.3"));
        consultaBuro.setCapacidadPago(capacidadPago);
        consultaBuroRepository.save(consultaBuro);

        AnalisisEnums.EvaluacionResultado resultado;
        AnalisisEnums.CategoriaRiesgo riesgo;
        String observaciones;

        if (capacidadPago.compareTo(solicitud.getCuotaMensual()) < 0) {
            resultado = AnalisisEnums.EvaluacionResultado.RECHAZADO;
            riesgo = AnalisisEnums.CategoriaRiesgo.ALTO;
            observaciones = "Capacidad de pago insuficiente";
        } else if (consultaBuro.getScoreExterno().compareTo(new BigDecimal("750")) > 0 && consultaBuro.getCuentasMorosas() == 0) {
            resultado = AnalisisEnums.EvaluacionResultado.APROBADO;
            riesgo = AnalisisEnums.CategoriaRiesgo.BAJO;
            observaciones = "Score alto y sin cuentas morosas";
        } else if (consultaBuro.getScoreExterno().compareTo(new BigDecimal("600")) >= 0 && consultaBuro.getScoreExterno().compareTo(new BigDecimal("750")) <= 0) {
            resultado = AnalisisEnums.EvaluacionResultado.EN_REVISION;
            riesgo = AnalisisEnums.CategoriaRiesgo.MEDIO;
            observaciones = "Score medio, requiere revisión manual";
        } else {
            resultado = AnalisisEnums.EvaluacionResultado.RECHAZADO;
            riesgo = AnalisisEnums.CategoriaRiesgo.ALTO;
            observaciones = "Score bajo o cuentas morosas";
        }

        // 4. Guardar evaluación
        EvaluacionCrediticia evaluacion = new EvaluacionCrediticia();
        evaluacion.setIdSolicitud(idSolicitud);
        evaluacion.setConsultaBuro(consultaBuro);
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setScoreInterno(null); // Si tienes lógica de score interno, calcula aquí
        evaluacion.setResultadoAutomatico(resultado);
        evaluacion.setCategoriaRiesgo(riesgo);
        evaluacion.setDecisionFinal(null);
        evaluacion.setJustificacionFinal(observaciones);
        evaluacion.setVersion(1);
        evaluacion = evaluacionCrediticiaRepository.save(evaluacion);

        return evaluacion;
    }

    /**
     * Módulo 4: Revisión del analista
     */
    @Transactional
    public ObservacionAnalista registrarRevisionAnalista(Integer idSolicitud, AnalisisEnums.DecisionAutoEnum decision, String justificacion) {
        Optional<EvaluacionCrediticia> evaluacionOpt = evaluacionCrediticiaRepository.findByIdSolicitud(idSolicitud);
        if (evaluacionOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe evaluación para la solicitud " + idSolicitud);
        }
        EvaluacionCrediticia evaluacion = evaluacionOpt.get();
        ObservacionAnalista obs = new ObservacionAnalista();
        obs.setEvaluacionCrediticia(evaluacion);
        obs.setDecisionAnalista(decision);
        obs.setJustificacion(justificacion);
        obs.setFechaHora(LocalDateTime.now());
        obs.setVersion(1);
        observacionAnalistaRepository.save(obs);
        // Actualiza la decisión final en la evaluación
        evaluacion.setDecisionFinal(
            decision == AnalisisEnums.DecisionAutoEnum.APROBADO ? AnalisisEnums.DecisionFinal.APROBADO :
            decision == AnalisisEnums.DecisionAutoEnum.RECHAZADO ? AnalisisEnums.DecisionFinal.RECHAZADO : null
        );
        evaluacion.setJustificacionFinal(justificacion);
        evaluacionCrediticiaRepository.save(evaluacion);
        return obs;
    }

    /**
     * Obtener evaluación crediticia por idSolicitud
     */
    public EvaluacionCrediticia obtenerEvaluacionPorSolicitud(Integer idSolicitud) {
        return evaluacionCrediticiaRepository.findByIdSolicitud(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe evaluación para la solicitud " + idSolicitud));
    }

    /**
     * Listar todas las evaluaciones crediticias
     */
    public java.util.List<EvaluacionCrediticia> listarEvaluaciones() {
        return evaluacionCrediticiaRepository.findAll();
    }
} 
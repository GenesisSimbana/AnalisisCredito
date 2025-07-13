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
        // 0. Verificar si ya existe una evaluación para esta solicitud
        Optional<EvaluacionCrediticia> evaluacionExistente = evaluacionCrediticiaRepository.findByIdSolicitud(idSolicitud);
        if (evaluacionExistente.isPresent()) {
            throw new IllegalStateException("Ya existe una evaluación para la solicitud " + idSolicitud + ". No se puede reevaluar automáticamente.");
        }

        // 1. Obtener datos de Originación
        SolicitudOriginacionDTO solicitud = originacionClient.obtenerSolicitud(idSolicitud.toString());
        if (!"PENDIENTE_EVALUACION".equalsIgnoreCase(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se puede evaluar solicitudes pendientes");
        }

        // 2. Obtener la cédula del cliente desde la solicitud
        String cedula = solicitud.getCedula();
        if (cedula == null || cedula.isEmpty()) {
            throw new IllegalStateException("No se pudo obtener la cédula del cliente desde la solicitud");
        }

        // 3. Consultar el cliente por cédula y obtener el score interno
        var cliente = originacionClient.obtenerClientePorCedula(cedula);
        BigDecimal scoreInterno = cliente.getScoreInterno();
        if (scoreInterno == null) {
            throw new IllegalStateException("No se pudo obtener el score interno del cliente");
        }

        // 4. Calcular capacidad de pago usando ingresos y egresos del cliente
        BigDecimal ingresos = cliente.getIngresos();
        BigDecimal egresos = cliente.getEgresos();
        
        if (ingresos == null || egresos == null) {
            throw new IllegalStateException("No se pudieron obtener los ingresos o egresos del cliente");
        }

        // Capacidad de pago = Ingresos - Egresos
        BigDecimal capacidadPago = ingresos.subtract(egresos);
        
        // Validar que la capacidad de pago sea positiva
        if (capacidadPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El cliente no tiene capacidad de pago positiva");
        }

        AnalisisEnums.EvaluacionResultado resultado;
        AnalisisEnums.CategoriaRiesgo riesgo;
        String observaciones;

        // 5. Lógica de decisión automática usando el score interno y capacidad de pago
        if (scoreInterno.compareTo(new BigDecimal("700")) > 0 && capacidadPago.compareTo(new BigDecimal("500")) > 0) {
            resultado = AnalisisEnums.EvaluacionResultado.APROBADO;
            riesgo = AnalisisEnums.CategoriaRiesgo.BAJO;
            observaciones = "Score interno alto (" + scoreInterno + ") y capacidad de pago adecuada (" + capacidadPago + "): aprobado automático";
        } else if (scoreInterno.compareTo(new BigDecimal("600")) >= 0 && capacidadPago.compareTo(new BigDecimal("300")) > 0) {
            resultado = AnalisisEnums.EvaluacionResultado.EN_REVISION;
            riesgo = AnalisisEnums.CategoriaRiesgo.MEDIO;
            observaciones = "Score interno medio (" + scoreInterno + ") y capacidad de pago moderada (" + capacidadPago + "): requiere revisión manual";
        } else {
            resultado = AnalisisEnums.EvaluacionResultado.RECHAZADO;
            riesgo = AnalisisEnums.CategoriaRiesgo.ALTO;
            observaciones = "Score interno bajo (" + scoreInterno + ") o capacidad de pago insuficiente (" + capacidadPago + "): rechazado automático";
        }

        // 6. Verificar si ya existe una consulta de buró para esta solicitud
        Optional<ConsultaBuro> consultaExistente = consultaBuroRepository.findByIdSolicitud(idSolicitud);
        if (consultaExistente.isPresent()) {
            throw new IllegalStateException("Ya existe una consulta de buró para la solicitud " + idSolicitud);
        }

        // 7. Crear y guardar consulta de buró con la capacidad de pago calculada
        ConsultaBuro consultaBuro = new ConsultaBuro();
        consultaBuro.setIdSolicitud(idSolicitud);
        consultaBuro.setFechaConsulta(LocalDateTime.now());
        consultaBuro.setCapacidadPago(capacidadPago);
        consultaBuro.setEstadoConsulta(AnalisisEnums.EstadoConsultaBuroEnum.EXITOSA);
        consultaBuro.setFuenteBuro("INTERNO");
        consultaBuro.setScoreExterno(scoreInterno); // Usamos scoreInterno como scoreExterno para simplificar
        consultaBuro.setCuentasActivas(0); // Valores por defecto ya que no tenemos datos de buró real
        consultaBuro.setCuentasMorosas(0);
        consultaBuro.setMontoTotalAdeudado(BigDecimal.ZERO);
        consultaBuro.setMontoMorosoTotal(BigDecimal.ZERO);
        consultaBuro.setDiasMoraPromedio(BigDecimal.ZERO);
        consultaBuro.setFechaPrimeraMora(LocalDate.now());
        consultaBuro.setVersion(1);
        consultaBuro = consultaBuroRepository.save(consultaBuro);

        // 8. Guardar evaluación
        EvaluacionCrediticia evaluacion = new EvaluacionCrediticia();
        evaluacion.setIdSolicitud(idSolicitud);
        evaluacion.setConsultaBuro(consultaBuro);
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setScoreInterno(scoreInterno);
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
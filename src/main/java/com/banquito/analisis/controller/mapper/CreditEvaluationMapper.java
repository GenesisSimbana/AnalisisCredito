package com.banquito.analisis.controller.mapper;

import com.banquito.analisis.controller.dto.CreditEvaluationDTO;
import com.banquito.analisis.model.EvaluacionCrediticia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CreditEvaluationMapper {

    @Mapping(source = "idSolicitud", target = "idSolicitud")
    @Mapping(source = "resultadoAutomatico", target = "estado")
    @Mapping(source = "consultaBuro.capacidadPago", target = "capacidadPago")
    @Mapping(source = "categoriaRiesgo", target = "nivelRiesgo")
    @Mapping(source = "resultadoAutomatico", target = "decisionAutomatica")
    @Mapping(source = "justificacionFinal", target = "observaciones")
    @Mapping(source = "justificacionFinal", target = "justificacionAnalista")
    CreditEvaluationDTO toDTO(EvaluacionCrediticia evaluacion);
} 
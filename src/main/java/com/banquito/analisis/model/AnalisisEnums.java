package com.banquito.analisis.model;

public class AnalisisEnums {

    public enum EvaluacionResultado {
        APROBADO, RECHAZADO, EN_REVISION
    }

    public enum CategoriaRiesgo {
        ALTO, MEDIO, BAJO
    }

    public enum DecisionFinal {
        APROBADO, RECHAZADO, AJUSTAR_CONDICIONES
    }

    public enum EstadoConsultaBuroEnum {
        PENDIENTE, EXITOSA, ERROR
    }

    public enum DecisionAutoEnum {
        APROBADO, RECHAZADO, REVISION_MANUAL
    }
} 
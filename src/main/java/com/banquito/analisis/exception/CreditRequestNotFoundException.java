package com.banquito.analisis.exception;

public class CreditRequestNotFoundException extends RuntimeException {

    private final Object idSolicitud;

    public CreditRequestNotFoundException(Object idSolicitud) {
        super("No se encontró ninguna evaluación o solicitud de crédito con id: " + idSolicitud);
        this.idSolicitud = idSolicitud;
    }

    public Object getIdSolicitud() {
        return idSolicitud;
    }
} 
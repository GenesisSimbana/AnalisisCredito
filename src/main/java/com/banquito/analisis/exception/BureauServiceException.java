package com.banquito.analisis.exception;

public class BureauServiceException extends RuntimeException {

    private final String operation;
    private final String details;

    public BureauServiceException(String operation, String details) {
        super("Error en el servicio de buró durante: " + operation + ". Detalles: " + details);
        this.operation = operation;
        this.details = details;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetails() {
        return details;
    }
} 
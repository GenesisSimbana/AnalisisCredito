package com.banquito.analisis.exception;

public class InvalidCreditRequestException extends RuntimeException {

    private final String field;
    private final String reason;

    public InvalidCreditRequestException(String field, String reason) {
        super("Solicitud de crédito inválida. Campo: " + field + ". Razón: " + reason);
        this.field = field;
        this.reason = reason;
    }

    public String getField() {
        return field;
    }

    public String getReason() {
        return reason;
    }
} 
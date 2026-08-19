package com.calidadsoftware.peliculas.model;

/** Cuerpo de respuesta uniforme para los errores que devuelve el controller. */
public class ErrorResponse {

    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

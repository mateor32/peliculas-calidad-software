package com.calidadsoftware.peliculas.exception;

/**
 * Se lanza cuando ocurre un problema de comunicacion (red, timeout, respuesta
 * invalida) con la API externa.
 */
public class MovieApiException extends RuntimeException {
    public MovieApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

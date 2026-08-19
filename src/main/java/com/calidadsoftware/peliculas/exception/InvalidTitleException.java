package com.calidadsoftware.peliculas.exception;

/**
 * Se lanza cuando el titulo recibido en la peticion esta vacio o es invalido.
 */
public class InvalidTitleException extends RuntimeException {
    public InvalidTitleException(String message) {
        super(message);
    }
}

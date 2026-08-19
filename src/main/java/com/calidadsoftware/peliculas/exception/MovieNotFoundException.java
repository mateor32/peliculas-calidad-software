package com.calidadsoftware.peliculas.exception;

/**
 * Se lanza cuando la API externa no encuentra ninguna pelicula con el titulo
 * dado.
 */
public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}

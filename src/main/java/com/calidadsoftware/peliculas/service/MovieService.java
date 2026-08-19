package com.calidadsoftware.peliculas.service;

import com.calidadsoftware.peliculas.client.MovieClient;
import com.calidadsoftware.peliculas.exception.InvalidTitleException;
import com.calidadsoftware.peliculas.exception.MovieNotFoundException;
import com.calidadsoftware.peliculas.model.Movie;
import org.springframework.stereotype.Service;

/**
 * Contiene la logica de negocio: validar la entrada e interpretar la respuesta
 * de la API.
 */
@Service
public class MovieService {

    private final MovieClient movieClient;

    public MovieService(MovieClient movieClient) {
        this.movieClient = movieClient;
    }

    public Movie searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidTitleException("El titulo de la pelicula no puede estar vacio");
        }

        Movie movie = movieClient.searchByTitle(title.trim());

        // OMDb responde 200 OK con Response=False cuando no encuentra coincidencias
        if (movie == null || "False".equalsIgnoreCase(movie.getResponse())) {
            throw new MovieNotFoundException("No se encontro ninguna pelicula con el titulo: " + title);
        }

        return movie;
    }
}

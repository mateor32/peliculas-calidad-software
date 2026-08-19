package com.calidadsoftware.peliculas.benchmark;

import com.calidadsoftware.peliculas.client.MovieClient;
import com.calidadsoftware.peliculas.model.Movie;
import org.springframework.web.client.RestTemplate;

/**
 * Doble de MovieClient sin red: devuelve siempre un resultado fijo en memoria.
 * Existe solo para que el microbenchmark mida la logica de MovieService,
 * no la latencia de una llamada HTTP real a OMDb.
 */
public class StubMovieClient extends MovieClient {

    private final Movie resultadoFijo;

    public StubMovieClient(Movie resultadoFijo) {
        super(new RestTemplate());
        this.resultadoFijo = resultadoFijo;
    }

    @Override
    public Movie searchByTitle(String title) {
        return resultadoFijo;
    }
}

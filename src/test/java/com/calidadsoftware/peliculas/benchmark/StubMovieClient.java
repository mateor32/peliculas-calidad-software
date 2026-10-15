package com.calidadsoftware.peliculas.benchmark;

import com.calidadsoftware.peliculas.client.MovieClient;
import com.calidadsoftware.peliculas.model.Movie;
import org.springframework.web.client.RestTemplate;

public class StubMovieClient extends MovieClient {

    private final Movie resultadoFijo;

    // Constructor para recibir resultado fijo a searchbytitle
    public StubMovieClient(Movie resultadoFijo) {
        super(new RestTemplate());
        this.resultadoFijo = resultadoFijo;
    }

    @Override
    public Movie searchByTitle(String title) {
        return resultadoFijo;
    }
}

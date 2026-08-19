package com.calidadsoftware.peliculas.benchmark;

import com.calidadsoftware.peliculas.exception.InvalidTitleException;
import com.calidadsoftware.peliculas.model.Movie;
import com.calidadsoftware.peliculas.service.MovieService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Microbenchmarks de MovieService.searchByTitle, aislado con StubMovieClient
 * (sin red). Compara el costo del camino feliz contra el costo de la ruta
 * que lanza InvalidTitleException.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MovieServiceBenchmark {

    private MovieService servicioConPeliculaEncontrada;
    private MovieService servicioParaTituloInvalido;

    @Setup
    public void setup() {
        Movie peliculaEncontrada = new Movie();
        peliculaEncontrada.setTitle("Matrix");
        peliculaEncontrada.setYear("1999");
        peliculaEncontrada.setResponse("True");

        servicioConPeliculaEncontrada = new MovieService(new StubMovieClient(peliculaEncontrada));
        servicioParaTituloInvalido = new MovieService(new StubMovieClient(null));
    }

    @Benchmark
    public Movie searchByTitle_tituloValido_peliculaEncontrada() {
        return servicioConPeliculaEncontrada.searchByTitle("Matrix");
    }

    @Benchmark
    public void searchByTitle_tituloVacio_lanzaExcepcion(Blackhole blackhole) {
        try {
            servicioParaTituloInvalido.searchByTitle("");
        } catch (InvalidTitleException e) {
            // se consume para evitar que el JIT elimine la llamada como "codigo muerto"
            blackhole.consume(e);
        }
    }
}

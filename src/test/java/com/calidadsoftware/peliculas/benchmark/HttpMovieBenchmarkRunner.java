package com.calidadsoftware.peliculas.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Punto de entrada para el benchmark HTTP (llama al microservicio real).
 * Requisito previo: la app debe estar corriendo en otra terminal:
 * mvn spring-boot:run
 * (con la variable de entorno OMDB_API_KEY configurada).
 *
 * Se usan menos iteraciones y warmup mas corto que en MovieServiceBenchmark
 * porque cada operacion aqui implica una llamada de red real, no solo
 * codigo en memoria.
 */
public class HttpMovieBenchmarkRunner {

    public static void main(String[] args) throws Exception {
        Options opciones = new OptionsBuilder()
                .include(HttpMovieBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.milliseconds(500))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(500))
                .forks(0)
                .threads(1)
                .build();

        new Runner(opciones).run();
    }
}

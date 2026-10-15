package com.calidadsoftware.peliculas.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        Options opciones = new OptionsBuilder()
                // las clases de benchmark que queremos ejecutar
                .include(MovieServiceBenchmark.class.getSimpleName())
                // iteraciones para opt
                .warmupIterations(5)
                // tiempo de cada iteracion
                .warmupTime(TimeValue.milliseconds(300))
                // 5 iteraciones
                .measurementIterations(5)
                // tiempo de la ||
                .measurementTime(TimeValue.milliseconds(300))
                // paraa no perder el estado del test
                .forks(0)
                .build();

        new Runner(opciones).run();
    }
}

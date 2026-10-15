package com.calidadsoftware.peliculas.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class HttpMovieBenchmarkRunner {

    public static void main(String[] args) throws Exception {
        Options opciones = new OptionsBuilder()
                // vamos a probar lo de la clase benchemark, vacio y valido
                .include(HttpMovieBenchmark.class.getSimpleName())
                // Calentar para que la JVM optimice el código antes de medir
                .warmupIterations(3)
                // Es para calentar durante 500ms cada iteracion
                .warmupTime(TimeValue.milliseconds(500))
                // Y vamos a medir 5 iteraciones
                .measurementIterations(5)
                // Es para medir durante 500ms cada iteracion
                .measurementTime(TimeValue.milliseconds(500))
                // para no crear ua nueva jvm y que no se pierda el estado de test.
                .forks(0)
                // Usamos un hilo
                .threads(1)
                .build();

        new Runner(opciones).run();
    }
}

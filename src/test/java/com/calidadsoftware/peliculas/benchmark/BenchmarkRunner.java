package com.calidadsoftware.peliculas.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Punto de entrada para correr los microbenchmarks sin plugins adicionales de
 * Maven.
 * Se usa forks(0) porque exec-maven-plugin no propaga el classpath de test a
 * una
 * JVM hija; para mediciones de produccion se recomendaria empaquetar un jar
 * ejecutable (maven-shade-plugin) y correrlo con forks(1) o mas.
 */
public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        Options opciones = new OptionsBuilder()
                .include(MovieServiceBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .warmupTime(TimeValue.milliseconds(300))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(300))
                .forks(0)
                .build();

        new Runner(opciones).run();
    }
}

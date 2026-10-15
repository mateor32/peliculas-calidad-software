package com.calidadsoftware.peliculas.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

// le dice al JMH que cree un solo objeto de esta clase y lo comparta entre
// todos los hilos (1 tenemos)
@State(Scope.Benchmark)
// jmh va a medir osea el tiempo promedio de cada operacion (129ms/op)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)

// Con esta clase vamos a medir cuanto tarda las peticiones http reales
public class HttpMovieBenchmark {
    // Aqui esta la app, y el titulo q vamos a buscar.
    private static final String BASE_URL = System.getProperty("benchmark.baseUrl", "http://localhost:8080");
    private static final String TITULO_VALIDO = System.getProperty("benchmark.title", "Matrix");
    // objeto http que vamos a comunicar
    private HttpClient httpClient;
    private URI uriTituloValido;
    private URI uriTituloVacio;

    @Setup(Level.Trial)
    public void setup() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        uriTituloValido = URI.create(BASE_URL + "/movies/search?title=" + TITULO_VALIDO);
        uriTituloVacio = URI.create(BASE_URL + "/movies/search?title=");

        // Falla rapido y con un mensaje claro si el servidor no esta arriba,
        // en vez de dejar que JMH reporte un monton de excepciones de conexion.
        try {
            httpClient.send(HttpRequest.newBuilder(uriTituloValido).GET().build(),
                    HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo conectar a " + BASE_URL + ". Levanta la app primero con: mvn spring-boot:run", e);
        }
    }

    // Metodo que va a medir cuanto tarda en buscar una pelicula valida y otra
    // vacia.
    @Benchmark
    public void searchByTitle_tituloValido_peliculaEncontrada(Blackhole blackhole) throws Exception {
        // Se hace una peticion get http.
        HttpResponse<String> response = httpClient.send(
                // Construye la peticion http con el titulo valido.
                HttpRequest.newBuilder(uriTituloValido).GET().build(),
                // recibe
                HttpResponse.BodyHandlers.ofString());
        // Resultado de la operacion
        blackhole.consume(response.statusCode());
        blackhole.consume(response.body());
    }

    // GET /movies/search?title=
    @Benchmark
    public void searchByTitle_tituloVacio_lanzaExcepcion(Blackhole blackhole) throws Exception {
        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(uriTituloVacio).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        blackhole.consume(response.statusCode());
        blackhole.consume(response.body());
    }
}

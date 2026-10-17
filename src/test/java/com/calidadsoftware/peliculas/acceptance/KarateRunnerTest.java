package com.calidadsoftware.peliculas.acceptance;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Levanta la app en un puerto aleatorio y lanza los escenarios Karate contra ella.
// Los escenarios etiquetados @omdb se omiten automaticamente si no esta definida
// la variable de entorno OMDB_API_KEY (asi no se intenta llamar al API real).
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "omdb.api.key=${OMDB_API_KEY:test-placeholder}"
)
class KarateRunnerTest {

    @LocalServerPort
    private int port;

    @Test
    void runAll() {
        System.setProperty("baseUrl", "http://localhost:" + port);

        String omdbKey = System.getenv("OMDB_API_KEY");
        boolean omdbAvailable = omdbKey != null && !omdbKey.isBlank();

        Runner.Builder<?> builder = Runner.path("classpath:acceptance")
                .outputHtmlReport(true)
                .outputCucumberJson(true);

        if (!omdbAvailable) {
            // Omite los escenarios que necesitan llamar a OMDb de verdad
            builder.tags("~@omdb");
        }

        Results results = builder.parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}

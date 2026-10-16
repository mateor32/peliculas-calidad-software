package com.calidadsoftware.peliculas.client;

import com.calidadsoftware.peliculas.exception.MovieApiException;
import com.calidadsoftware.peliculas.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;

// Pruebas unitarias de MovieClient usando MockRestServiceServer:
// nunca llaman a OMDb de verdad ni necesitan OMDB_API_KEY.

class MovieClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private MovieClient movieClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        movieClient = new MovieClient(restTemplate);
        // @Value no se inyecta sin contexto Spring; los seteamos a mano
        ReflectionTestUtils.setField(movieClient, "apiUrl", "https://api.omdb-fake.test");
        ReflectionTestUtils.setField(movieClient, "apiKey", "FAKEKEY");
    }

    @Test
    @DisplayName("1. Respuesta exitosa: OMDb devuelve JSON de la pelicula y el client lo mapea a Movie")
    void searchByTitle_deberiaRetornarMovie_cuandoOmdbRespondeConLaPelicula() {
        String json = "{"
                + "\"Title\":\"Matrix\","
                + "\"Year\":\"1999\","
                + "\"imdbID\":\"tt0133093\","
                + "\"Type\":\"movie\","
                + "\"Poster\":\"https://poster.example/img.jpg\","
                + "\"Response\":\"True\""
                + "}";
        mockServer.expect(requestTo("https://api.omdb-fake.test?apikey=FAKEKEY&t=Matrix"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        Movie resultado = movieClient.searchByTitle("Matrix");

        assertNotNull(resultado);
        assertEquals("Matrix", resultado.getTitle());
        assertEquals("1999", resultado.getYear());
        assertEquals("tt0133093", resultado.getImdbId());
        assertEquals("movie", resultado.getType());
        assertEquals("https://poster.example/img.jpg", resultado.getPoster());
        assertEquals("True", resultado.getResponse());
        mockServer.verify();
    }

    @Test
    @DisplayName("2. Pelicula no encontrada: OMDb responde 200 con Response=False y el client lo mapea sin lanzar")
    void searchByTitle_deberiaRetornarMovieConResponseFalse_cuandoOmdbNoEncuentraLaPelicula() {
        String json = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";
        mockServer.expect(requestTo("https://api.omdb-fake.test?apikey=FAKEKEY&t=NoExiste"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        Movie resultado = movieClient.searchByTitle("NoExiste");

        assertNotNull(resultado);
        assertEquals("False", resultado.getResponse());
        assertEquals("Movie not found!", resultado.getError());
        mockServer.verify();
    }

    @Test
    @DisplayName("3. Error de la API externa: el client envuelve la causa en MovieApiException")
    void searchByTitle_deberiaLanzarMovieApiException_cuandoRestTemplateFalla() {
        mockServer.expect(requestTo("https://api.omdb-fake.test?apikey=FAKEKEY&t=Matrix"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        MovieApiException ex = assertThrows(MovieApiException.class,
                () -> movieClient.searchByTitle("Matrix"));
        assertEquals("No se pudo comunicar con la API externa de peliculas", ex.getMessage());
        assertNotNull(ex.getCause());
        mockServer.verify();
    }
}

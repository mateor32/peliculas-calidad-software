package com.calidadsoftware.peliculas.controller;

import com.calidadsoftware.peliculas.exception.InvalidTitleException;
import com.calidadsoftware.peliculas.exception.MovieApiException;
import com.calidadsoftware.peliculas.exception.MovieNotFoundException;
import com.calidadsoftware.peliculas.model.Movie;
import com.calidadsoftware.peliculas.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {
    // MockMvc simula las peticiones HTTP sin levantar un servidor real
    @Autowired
    private MockMvc mockMvc;
    // MockBean reemplaza service por un mockito
    @MockBean
    private MovieService movieService;

    @Test
    @DisplayName("1. Pelicula encontrada: GET /movies/search?title=Matrix devuelve 200 con la pelicula")
    void searchByTitle_deberiaRetornar200ConLaPelicula_cuandoMovieServiceLaEncuentra() throws Exception {
        // Arrange
        // ayudaa armar un Movie de prueba
        Movie peliculaEncontrada = new Movie();
        peliculaEncontrada.setTitle("Matrix");
        peliculaEncontrada.setYear("1999");
        peliculaEncontrada.setImdbId("tt0133093");
        peliculaEncontrada.setType("movie");
        peliculaEncontrada.setResponse("True");
        when(movieService.searchByTitle("Matrix")).thenReturn(peliculaEncontrada);

        // Act
        // Simulamos la peticion HTTP GET /movies/search?title=Matrix
        ResultActions response = mockMvc.perform(get("/movies/search").param("title", "Matrix"));

        // Assert
        // Comprobamos que la respuesta tenga status 200 OK y el JSON esperado
        // response.andExpect(status().isNotFound())
        response.andExpect(status().isOk()) // real
                .andExpect(jsonPath("$.Title").value("Matrix"))
                .andExpect(jsonPath("$.Response").value("True"));
        verify(movieService).searchByTitle("Matrix");
    }

    @Test
    @DisplayName("2. Pelicula no encontrada: MovieService lanza MovieNotFoundException y el controller responde 404")
    void searchByTitle_deberiaRetornar404_cuandoMovieServiceLanzaMovieNotFoundException() throws Exception {
        // Arrange
        String titulo = "PeliculaQueNoExiste";
        String mensajeError = "No se encontro ninguna pelicula con el titulo: " + titulo;
        when(movieService.searchByTitle(titulo)).thenThrow(new MovieNotFoundException(mensajeError));

        // Act
        ResultActions response = mockMvc.perform(get("/movies/search").param("title", titulo));

        // Assert
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(mensajeError));
        verify(movieService).searchByTitle(titulo);
    }

    @Test
    @DisplayName("3. Titulo invalido: MovieService lanza InvalidTitleException y el controller responde 400")
    void searchByTitle_deberiaRetornar400_cuandoMovieServiceLanzaInvalidTitleException() throws Exception {
        // Arrange
        String tituloVacio = "";
        String mensajeError = "El titulo de la pelicula no puede estar vacio";
        when(movieService.searchByTitle(tituloVacio)).thenThrow(new InvalidTitleException(mensajeError));

        // Act
        ResultActions response = mockMvc.perform(get("/movies/search").param("title", tituloVacio));

        // Assert
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(mensajeError));
        verify(movieService).searchByTitle(tituloVacio);
    }

    @Test
    @DisplayName("4. Parametro title ausente: Spring lanza MissingServletRequestParameterException y el controller responde 400")
    void searchByTitle_deberiaRetornar400_cuandoFaltaElParametroTitle() throws Exception {
        // Act: no mandamos el query param "title"
        ResultActions response = mockMvc.perform(get("/movies/search"));

        // Assert: 400 con el mensaje fijo del handler y sin tocar el service
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El parametro 'title' es obligatorio"));
        verify(movieService, never()).searchByTitle(anyString());
    }

    @Test
    @DisplayName("5. Error de la API externa: MovieService lanza MovieApiException y el controller responde 502")
    void searchByTitle_deberiaRetornar502_cuandoMovieServiceLanzaMovieApiException() throws Exception {
        // Arrange
        String titulo = "Matrix";
        String mensajeError = "No se pudo comunicar con la API externa de peliculas";
        when(movieService.searchByTitle(titulo))
                .thenThrow(new MovieApiException(mensajeError, new RuntimeException("Timeout")));

        // Act
        ResultActions response = mockMvc.perform(get("/movies/search").param("title", titulo));

        // Assert
        response.andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value(mensajeError));
        verify(movieService).searchByTitle(titulo);
    }
}

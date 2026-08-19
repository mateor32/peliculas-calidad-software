package com.calidadsoftware.peliculas.controller;

import com.calidadsoftware.peliculas.exception.InvalidTitleException;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de MovieController en aislamiento: MovieService se
 * reemplaza
 * por un mock de Mockito (@MockBean), por lo que ningun test ejecuta la logica
 * real de negocio ni llama a la API externa. MockMvc simula las peticiones HTTP
 * sin levantar un servidor real.
 */
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    @DisplayName("1. Pelicula encontrada: GET /movies/search?title=Matrix devuelve 200 con la pelicula")
    void searchByTitle_deberiaRetornar200ConLaPelicula_cuandoMovieServiceLaEncuentra() throws Exception {
        // Arrange
        Movie peliculaEncontrada = new Movie();
        peliculaEncontrada.setTitle("Matrix");
        peliculaEncontrada.setYear("1999");
        peliculaEncontrada.setImdbId("tt0133093");
        peliculaEncontrada.setType("movie");
        peliculaEncontrada.setResponse("True");
        when(movieService.searchByTitle("Matrix")).thenReturn(peliculaEncontrada);

        // Act
        ResultActions response = mockMvc.perform(get("/movies/search").param("title", "Matrix"));

        // Assert
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
}

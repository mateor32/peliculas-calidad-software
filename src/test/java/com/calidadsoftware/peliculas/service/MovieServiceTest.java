package com.calidadsoftware.peliculas.service;

import com.calidadsoftware.peliculas.client.MovieClient;
import com.calidadsoftware.peliculas.exception.InvalidTitleException;
import com.calidadsoftware.peliculas.exception.MovieApiException;
import com.calidadsoftware.peliculas.exception.MovieNotFoundException;
import com.calidadsoftware.peliculas.model.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Pruebas unitarias de MovieService en aislamiento: MovieClient se reemplaza

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    // SImular api
    @Mock
    private MovieClient movieClient;
    // Ponemos en el service el mock
    @InjectMocks
    private MovieService movieService;

    // Metodo que ayuda a crear un mockito de prueba
    private Movie buildMovie(String title, String response) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setYear("1999");
        movie.setImdbId("tt0133093");
        movie.setType("movie");
        movie.setPoster("https://example.com/poster.jpg");
        movie.setResponse(response);
        return movie;
    }

    @Test
    @DisplayName("1. Pelicula encontrada: MovieClient devuelve Response=True y el service la retorna")
    void searchByTitle_deberiaRetornarPelicula_cuandoMovieClientEncuentraCoincidencia() {
        // Arrange
        String titulo = "Matrix";
        // cuando el movieClient busque por titulo devuelva la pelicula encontrada
        Movie peliculaEncontrada = buildMovie("Matrix", "True");
        when(movieClient.searchByTitle(titulo)).thenReturn(peliculaEncontrada);

        // Act
        // Llamamos al metodo
        Movie resultado = movieService.searchByTitle(titulo);

        // Assert
        assertNotNull(resultado);
        // Verificamos que el resultado sea el esperado
        assertEquals("Matrix", resultado.getTitle());

        assertEquals("True", resultado.getResponse());
        // Que si haya sido llamado por el titulo
        verify(movieClient).searchByTitle(titulo);
    }

    @Test
    @DisplayName("2. Pelicula no encontrada: MovieClient devuelve Response=False y el service lanza MovieNotFoundException")
    void searchByTitle_deberiaLanzarMovieNotFoundException_cuandoMovieClientRespondeFalse() {
        // Arrange
        String titulo = "PeliculaQueNoExiste";
        Movie peliculaNoEncontrada = buildMovie(null, "False");
        peliculaNoEncontrada.setError("Movie not found!");
        when(movieClient.searchByTitle(titulo)).thenReturn(peliculaNoEncontrada);

        // Act
        Executable accion = () -> movieService.searchByTitle(titulo);

        // Assert
        MovieNotFoundException exception = assertThrows(MovieNotFoundException.class, accion);
        assertEquals("No se encontro ninguna pelicula con el titulo: " + titulo, exception.getMessage());
        verify(movieClient).searchByTitle(titulo);
    }

    @Test
    @DisplayName("3. Titulo vacio o invalido: el service lanza InvalidTitleException sin llamar a MovieClient")
    void searchByTitle_deberiaLanzarInvalidTitleException_cuandoTituloEsVacioOInvalido() {
        // Arrange
        String tituloVacio = "";
        String tituloEnBlanco = "   ";
        String tituloNulo = null;

        // Act
        Executable buscarConTituloVacio = () -> movieService.searchByTitle(tituloVacio);
        Executable buscarConTituloEnBlanco = () -> movieService.searchByTitle(tituloEnBlanco);
        Executable buscarConTituloNulo = () -> movieService.searchByTitle(tituloNulo);

        // Assert
        assertThrows(InvalidTitleException.class, buscarConTituloVacio);
        assertThrows(InvalidTitleException.class, buscarConTituloEnBlanco);
        assertThrows(InvalidTitleException.class, buscarConTituloNulo);

        // MovieClient nunca debe ser invocado porque la validacion ocurre antes
        verify(movieClient, never()).searchByTitle(anyString());
    }

    @Test
    @DisplayName("4. Error del servicio externo: MovieClient lanza MovieApiException y el service la propaga")
    void searchByTitle_deberiaPropagarMovieApiException_cuandoMovieClientFalla() {
        // Arrange
        String titulo = "Matrix";
        MovieApiException errorDeComunicacion = new MovieApiException(
                "No se pudo comunicar con la API externa de peliculas",
                new RuntimeException("Timeout"));
        when(movieClient.searchByTitle(titulo)).thenThrow(errorDeComunicacion);

        // Act
        Executable accion = () -> movieService.searchByTitle(titulo);

        // Assert
        MovieApiException exception = assertThrows(MovieApiException.class, accion);
        assertEquals("No se pudo comunicar con la API externa de peliculas", exception.getMessage());
        verify(movieClient).searchByTitle(titulo);
    }

    @Test
    @DisplayName("5. Respuesta invalida o incompleta: MovieClient devuelve null y el service lanza MovieNotFoundException")
    void searchByTitle_deberiaLanzarMovieNotFoundException_cuandoMovieClientRetornaNull() {
        // Arrange
        String titulo = "Matrix";
        when(movieClient.searchByTitle(titulo)).thenReturn(null);

        // Act
        Executable accion = () -> movieService.searchByTitle(titulo);

        // Assert
        assertThrows(MovieNotFoundException.class, accion);
        verify(movieClient).searchByTitle(titulo);
    }
}

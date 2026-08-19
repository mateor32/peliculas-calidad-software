package com.calidadsoftware.peliculas.controller;

import com.calidadsoftware.peliculas.exception.InvalidTitleException;
import com.calidadsoftware.peliculas.exception.MovieApiException;
import com.calidadsoftware.peliculas.exception.MovieNotFoundException;
import com.calidadsoftware.peliculas.model.ErrorResponse;
import com.calidadsoftware.peliculas.model.Movie;
import com.calidadsoftware.peliculas.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recibe las peticiones HTTP y delega toda la logica de negocio a MovieService.
 */
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public ResponseEntity<Movie> searchByTitle(@RequestParam String title) {
        Movie movie = movieService.searchByTitle(title);
        return ResponseEntity.ok(movie);
    }

    // title vacio o solo espacios -> 400 Bad Request
    @ExceptionHandler(InvalidTitleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTitle(InvalidTitleException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    // falta el parametro "title" en la URL -> 400 Bad Request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("El parametro 'title' es obligatorio"));
    }

    // no existe ninguna pelicula con ese titulo -> 404 Not Found
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(MovieNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    // fallo de red/timeout/etc. al llamar a la API externa -> 502 Bad Gateway
    @ExceptionHandler(MovieApiException.class)
    public ResponseEntity<ErrorResponse> handleApiError(MovieApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(e.getMessage()));
    }
}

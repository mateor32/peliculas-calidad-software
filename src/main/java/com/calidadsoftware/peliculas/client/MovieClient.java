package com.calidadsoftware.peliculas.client;

import com.calidadsoftware.peliculas.exception.MovieApiException;
import com.calidadsoftware.peliculas.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Unica clase que sabe como hablar con la API externa de peliculas (OMDb).
 * El resto de la aplicacion no conoce detalles de la API (URL, api key, formato
 * JSON).
 */
@Component
public class MovieClient {

    private final RestTemplate restTemplate;

    @Value("${omdb.api.url}")
    private String apiUrl;

    @Value("${omdb.api.key}")
    private String apiKey;

    public MovieClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Llama a OMDb pidiendo la pelicula por titulo exacto (parametro "t").
     * OMDb siempre responde 200 OK, incluso cuando no encuentra la pelicula
     * (en ese caso trae Response=False y un campo Error), por lo que esta capa
     * solo se preocupa de errores de transporte/comunicacion, no del "no
     * encontrado".
     */
    public Movie searchByTitle(String title) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("t", title)
                .toUriString();

        try {
            return restTemplate.getForObject(url, Movie.class);
        } catch (RestClientException e) {
            throw new MovieApiException("No se pudo comunicar con la API externa de peliculas", e);
        }
    }
}

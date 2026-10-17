Feature: GET /movies/search - pruebas de aceptacion

  Background:
    * def baseUrl = karate.properties['baseUrl']
    * url baseUrl + '/movies/search'

  Scenario: Sin parametro title -> 400
    When method GET
    Then status 400
    And match response.message == "El parametro 'title' es obligatorio"

  Scenario: Titulo vacio -> 400
    Given param title = ''
    When method GET
    Then status 400
    And match response.message == 'El titulo de la pelicula no puede estar vacio'

  @omdb
  Scenario: Titulo existente (Inception) -> 200 con la pelicula
    Given param title = 'Inception'
    When method GET
    Then status 200
    And match response.Title == 'Inception'
    And match response.Response == 'True'

  @omdb
  Scenario: Titulo inexistente -> 404
    Given param title = 'zzzxxxqqq123'
    When method GET
    Then status 404
    And match response.message contains 'No se encontro'

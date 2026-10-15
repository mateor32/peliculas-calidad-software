# Peliculas Backend (Calidad de Software)

Backend sencillo en Spring Boot que permite buscar peliculas por titulo consumiendo
la API externa [OMDb](https://www.omdbapi.com/).

## Arquitectura

```
Usuario -> MovieController -> MovieService -> MovieClient -> OMDb (API externa)
```

- `controller/MovieController`: expone `GET /movies/search?title=...` y traduce las
  excepciones de negocio a codigos HTTP.
- `service/MovieService`: valida la entrada e interpreta la respuesta de la API.
- `client/MovieClient`: unico componente que conoce la URL, la api key y el formato
  JSON de OMDb.
- `model/Movie`: representa los datos de la pelicula.
- `model/ErrorResponse`: cuerpo de error uniforme.
- `exception/*`: `InvalidTitleException`, `MovieNotFoundException`, `MovieApiException`.

## 1. Obtener la API key de OMDb

1. Ir a https://www.omdbapi.com/apikey.aspx
2. Elegir el plan "FREE" (1000 peticiones/dia) e ingresar un correo.
3. OMDb envia la key al correo (llega en minutos). Es un texto corto, por ejemplo `a1b2c3d4`.

## 2. Configurar la API key como variable de entorno

La aplicacion la lee desde `OMDB_API_KEY` (ver `src/main/resources/application.properties`).
Nunca se escribe la key directamente en el codigo ni en el repositorio.

**Windows PowerShell (sesion actual):**

```powershell
$env:OMDB_API_KEY = "tu_api_key_aqui"
```

**Windows PowerShell (permanente para tu usuario):**

```powershell
setx OMDB_API_KEY "tu_api_key_aqui"
```

(cierra y vuelve a abrir la terminal para que tome efecto)

**Linux/macOS:**

```bash
export OMDB_API_KEY=tu_api_key_aqui
```

## 3. Ejecutar el proyecto

Desde la carpeta raiz del proyecto, usando el Gradle Wrapper (no requiere Gradle instalado):

**Windows PowerShell:**

```powershell
.\gradlew.bat bootRun
```

**Linux/macOS:**

```bash
./gradlew bootRun
```

La aplicacion levanta en `http://localhost:8080`.

Para ejecutar las pruebas unitarias:

```powershell
.\gradlew.bat test
```

(En Linux/macOS: `./gradlew test`.)

## 4. Probar el endpoint

**Pelicula encontrada:**

```
GET http://localhost:8080/movies/search?title=Matrix
```

Respuesta 200 OK con el JSON de la pelicula.

**Pelicula no encontrada:**

```
GET http://localhost:8080/movies/search?title=asdkjhaskjdh
```

Respuesta 404 con `{ "message": "No se encontro ninguna pelicula con el titulo: ..." }`

**Titulo vacio:**

```
GET http://localhost:8080/movies/search?title=
```

Respuesta 400.

**Parametro faltante:**

```
GET http://localhost:8080/movies/search
```

Respuesta 400.

Con curl:

```bash
curl "http://localhost:8080/movies/search?title=Matrix"
```

O usando el archivo `.http`/Postman/Insomnia con la misma URL.

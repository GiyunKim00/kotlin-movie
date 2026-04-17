package movie.api.controller

import movie.api.dto.MoviesResponse
import movie.api.service.MovieApiService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/movies")
class MovieApiController(
    private val movieApiService: MovieApiService,
) {
    @GetMapping
    fun getMovies(): MoviesResponse = movieApiService.getMovies()
}

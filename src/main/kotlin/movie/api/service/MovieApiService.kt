package movie.api.service

import movie.api.dto.MovieResponse
import movie.api.dto.MoviesResponse
import movie.api.dto.ScreeningResponse
import movie.repository.ScreeningRepository
import org.springframework.stereotype.Service

@Service
class MovieApiService(
    private val screeningRepository: ScreeningRepository,
) {
    fun getMovies(): MoviesResponse {
        val movies = screeningRepository.findAllMoviesWithScreenings()
            .map { movieScreenings ->
                val movie = movieScreenings.movie
                MovieResponse(
                    id = requireNotNull(movie.id),
                    title = movie.title.value,
                    runningTimeMinutes = movie.runningTime.value,
                    screenings = movieScreenings.screenings.map { screening ->
                        ScreeningResponse(
                            id = requireNotNull(screening.id),
                            startAt = screening.startTime.value,
                            endAt = screening.endTime(),
                        )
                    },
                )
            }

        return MoviesResponse(movies)
    }
}
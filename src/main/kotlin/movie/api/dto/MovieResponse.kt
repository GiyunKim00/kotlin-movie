package movie.api.dto

import java.time.LocalDateTime

data class MoviesResponse(
    val movies: List<MovieResponse>,
)

data class MovieResponse(
    val id: Long,
    val title: String,
    val runningTimeMinutes: Int,
    val screenings: List<ScreeningResponse>,
)

data class ScreeningResponse(
    val id: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)
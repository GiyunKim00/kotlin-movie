package movie.service

import movie.domain.screening.Movie
import movie.domain.screening.Screening

data class MovieScreenings(
    val movie: Movie,
    val screenings: List<Screening>,
)
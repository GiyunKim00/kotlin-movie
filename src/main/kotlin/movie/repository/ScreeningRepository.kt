package movie.repository

import movie.api.service.MovieScreenings
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import java.time.LocalDate

interface ScreeningRepository {
    fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening>

    fun findSameScreening(screening: Screening): Screening?

    fun reserveSeats(
        screening: Screening,
        selectedSeats: Seats,
    )

    fun findAllMoviesWithScreenings(): List<MovieScreenings>

    fun findScreeningById(screeningId: Long): Screening?
}

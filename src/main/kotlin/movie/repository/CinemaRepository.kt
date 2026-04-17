package movie.repository

import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.service.MovieScreenings
import java.time.LocalDate

class CinemaRepository(
    private var screenings: List<Screening>,
) : ScreeningRepository {
    override fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening> =
        screenings
            .filter {
                it.isSameMovie(title) && it.isSameDate(date)
            }.sortedBy { it.startTime.value }

    override fun findSameScreening(screening: Screening): Screening? =
        screenings.firstOrNull {
            it.isSameScreening(screening)
        }

    override fun reserveSeats(
        screening: Screening,
        selectedSeats: Seats,
    ) {
        screenings =
            screenings.map {
                if (it.isSameScreening(screening)) {
                    it.reserve(selectedSeats)
                } else {
                    it
                }
            }
    }

    override fun findAllMoviesWithScreenings(): List<MovieScreenings> =
        screenings
            .groupBy { it.movie }
            .map { (movie, screenings) ->
                MovieScreenings(
                    movie = movie,
                    screenings = screenings.sortedBy { it.startTime.value },
                )
            }

    override fun findScreeningById(screeningId: Long): Screening? = screenings.firstOrNull { it.id == screeningId }
}

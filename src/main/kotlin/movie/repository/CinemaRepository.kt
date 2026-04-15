package movie.repository

import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import java.time.LocalDate

class CinemaRepository(
    var screenings: List<Screening>,
) {
    fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening> =
        screenings
            .filter {
                it.isSameMovie(title) && it.isSameDate(date)
            }.sortedBy { it.startTime.value }

    fun findSameScreening(screening: Screening): Screening? =
        screenings.firstOrNull {
            it.isSameScreening(screening)
        }

    fun reserveSeats(
        screening: Screening,
        selectedSeats: Seats,
    ) {
        screenings = screenings.map {
            if (it.isSameScreening(screening)) {
                it.reserve(selectedSeats)
            } else {
                it
            }
        }
    }
}

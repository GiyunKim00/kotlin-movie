package movie.domain.screening

import movie.domain.reservation.Seat
import movie.domain.reservation.Seats
import java.time.LocalDate
import java.time.LocalDateTime

class Screening(
    val movie: Movie,
    val startTime: ScreeningStartTime,
    private val reservedSeats: Seats = Seats(emptyList()),
) {
    fun reserve(seats: Seats): Screening {
        require(!hasReservedSeat(seats)) { "이미 예약된 좌석은 다시 선택할 수 없습니다." }
        return Screening(
            movie = movie,
            startTime = startTime,
            reservedSeats = reservedSeats + seats,
        )
    }

    fun isReserved(seat: Seat): Boolean = reservedSeats.contains(seat)

    fun hasReservedSeat(seats: Seats): Boolean = seats.any { isReserved(it) }

    fun reservedSeatsFrom(seats: Seats): Seats = seats.filter { isReserved(it) }

    fun isSameScreening(other: Screening): Boolean = movie == other.movie && startTime == other.startTime

    fun isSameMovie(other: String): Boolean = movie.isSameTitle(other)

    fun isSameDate(date: LocalDate): Boolean = startTime.value.toLocalDate() == date

    fun endTime(): LocalDateTime = startTime.value.plusMinutes(movie.runningTime.value.toLong())

    fun overlaps(otherScreen: Screening): Boolean = startTime.value < otherScreen.endTime() && otherScreen.startTime.value < endTime()
}

@JvmInline
value class ScreeningStartTime(
    val value: LocalDateTime,
)

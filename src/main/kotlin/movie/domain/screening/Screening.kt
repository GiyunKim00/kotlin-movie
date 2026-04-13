package movie.domain.screening

import movie.domain.reservation.Seat
import java.time.LocalDateTime

class Screening(
    val movie: Movie,
    val startTime: ScreeningStartTime,
    private val reservedSeats: List<Seat> = emptyList(),
) {
    fun isReserved(seat: Seat): Boolean = reservedSeats.contains(seat)

    fun reserve(seats: List<Seat>): Screening {
        require(seats.none { isReserved(it) }) { "이미 예약된 좌석은 다시 선택할 수 없습니다." }
        return Screening(
            movie = movie,
            startTime = startTime,
            reservedSeats = reservedSeats + seats,
        )
    }
    fun endTime(): LocalDateTime = startTime.value.plusMinutes(movie.runningTime.value.toLong())

    fun overlaps(otherScreen: Screening): Boolean = startTime.value < otherScreen.endTime() && otherScreen.startTime.value < endTime()
}

@JvmInline
value class ScreeningStartTime(
    val value: LocalDateTime,
)

package domain.screening

import domain.reservation.Seat
import java.time.LocalDateTime

class Screening private constructor(
    private val movie: Movie,
    val startTime: ScreeningStartTime,
    private val reservedSeats: List<ReservedSeats>
) {
    fun isReserved(seat: Seat): Boolean = reservedSeats.contains(ReservedSeats(seat))

    fun isMovie(movie: Movie): Boolean = this.movie == movie

    fun price(): Int = reservedSeats.sumOf { it.seat.grade.money }
}

@JvmInline
value class ScreeningStartTime(val value: LocalDateTime)
private class ReservedSeats(val seat: Seat)


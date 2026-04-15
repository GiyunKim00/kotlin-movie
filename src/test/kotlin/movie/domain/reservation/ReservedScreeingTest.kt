package movie.domain.reservation

import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ReservedScreeingTest {
    private val seats = Seats(listOf(Seat(SeatRow("A"), SeatColumn(1))))
    private val reservedSeats =
        Screening(
            movie =
                Movie(
                    MovieTitle("어벤져스"),
                    RunningTime(120),
                ),
            startTime = ScreeningStartTime(LocalDateTime.now()),
            reservedSeats = seats,
        )

    @Test
    fun `예약된 좌석을 확인한다`() {
        assertTrue(reservedSeats.isReserved(Seat(SeatRow("A"), SeatColumn(1))))
    }

    @Test
    fun `예약이 되어 있지 않다면 False를 반환한다`() {
        assertTrue(!reservedSeats.isReserved(Seat(SeatRow("B"), SeatColumn(1))))
    }
}

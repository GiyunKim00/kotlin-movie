package domain.reservation

import domain.screening.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ReservedScreeingTest {
    @Test
    fun `예약된 좌석을 확인한다`() {
        val seats = listOf(Seat(SeatRow("A"), SeatColumn(1), SeatGrade.S))
        val reservedSeats = Screening.create(
            movie = Movie(
                MovieTitle("어벤져스"),
                RunningTime(120)
            ),
            startTime = ScreeningStartTime(LocalDateTime.now()),
            reservedSeats = seats
        )

        assertTrue(reservedSeats.isReserved(Seat(SeatRow("A"), SeatColumn(1), SeatGrade.S)))
    }

    @Test
    fun `예약이 되어 있지 않다면 False를 반환한다`() {
        val seats = listOf(Seat(SeatRow("A"), SeatColumn(1), SeatGrade.S))
        val reservedSeats = Screening.create(
            movie = Movie(
                MovieTitle("어벤져스"),
                RunningTime(120)
            ),
            startTime = ScreeningStartTime(LocalDateTime.now()),
            reservedSeats = seats
        )

        assertTrue(!reservedSeats.isReserved(Seat(SeatRow("B"), SeatColumn(1), SeatGrade.S)))
    }
}
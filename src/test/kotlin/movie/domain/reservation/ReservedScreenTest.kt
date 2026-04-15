package movie.domain.reservation

import movie.domain.payment.Money
import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ReservedScreenTest {
    private val seats = Seats(listOf(Seat(SeatRow("A"), SeatColumn(1))))
    private val reservedScreen =
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
    fun `선택한 좌석들의 총 가격을 반환한다`() {
        // given
        val reservedScreen =
            ReservedScreen(
                screen = reservedScreen,
                seats = seats,
            )

        // when & then
        assertThat(reservedScreen.price()).isEqualTo(Money(12_000))
    }

    @Test
    fun `예약된 좌석을 확인한다`() {
        assertThat(reservedScreen.isReserved(Seat(SeatRow("A"), SeatColumn(1)))).isTrue
    }

    @Test
    fun `예약이 되어 있지 않다면 False를 반환한다`() {
        assertThat(!reservedScreen.isReserved(Seat(SeatRow("B"), SeatColumn(1)))).isTrue
    }
}

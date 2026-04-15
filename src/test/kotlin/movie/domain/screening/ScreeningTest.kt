package movie.domain.screening

import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatRow
import movie.domain.reservation.Seats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test

class ScreeningTest {
    private fun screening(
        title: String = "인터스텔라",
        startTime: LocalDateTime,
        runningTime: Int = 120,
        reservedSeats: Seats = Seats(emptyList()),
    ): Screening =
        Screening(
            movie =
                Movie(
                    title = MovieTitle(title),
                    runningTime = RunningTime(runningTime),
                ),
            startTime = ScreeningStartTime(startTime),
            reservedSeats = reservedSeats,
        )

    @Test
    fun `빈 좌석을 예약하면 새 상영 객체에 좌석이 추가된다`() {
        val seat = Seat(SeatRow("A"), SeatColumn(1))
        val screening = screening(startTime = LocalDateTime.of(2026, 5, 1, 10, 0))

        val reserved = screening.reserve(Seats(listOf(seat)))

        assertThat(reserved.isReserved(seat)).isTrue()
        assertThat(screening.isReserved(seat)).isFalse()
    }

    @Test
    fun `예약된 좌석을 예약 시도할 시 예외가 발생한다`() {
        val seat = Seat(SeatRow("A"), SeatColumn(1))
        val screening =
            screening(
                startTime = LocalDateTime.of(2026, 5, 1, 10, 0),
                reservedSeats = Seats(listOf(seat)),
            )

        val exception =
            assertThrows<IllegalArgumentException> {
                screening.reserve(Seats(listOf(seat)))
            }

        assertThat(exception.message).isEqualTo("이미 예약된 좌석은 다시 선택할 수 없습니다.")
    }

    @Test
    fun `예약 좌석이 하나라도 포함되어 있으면 true를 반환한다`() {
        val reservedSeat = Seat(SeatRow("A"), SeatColumn(1))
        val newSeat = Seat(SeatRow("B"), SeatColumn(1))
        val screening =
            screening(
                startTime = LocalDateTime.of(2026, 5, 1, 10, 0),
                reservedSeats = Seats(listOf(reservedSeat)),
            )

        val result = screening.hasReservedSeat(Seats(listOf(reservedSeat, newSeat)))

        assertThat(result).isTrue()
    }

    @Test
    fun `좌석 중 예약된 좌석만 반환한다`() {
        val reservedSeat = Seat(SeatRow("A"), SeatColumn(1))
        val notReservedSeat = Seat(SeatRow("B"), SeatColumn(1))
        val screening =
            screening(
                startTime = LocalDateTime.of(2026, 5, 1, 10, 0),
                reservedSeats = Seats(listOf(reservedSeat)),
            )

        val result = screening.reservedSeatsFrom(Seats(listOf(reservedSeat, notReservedSeat)))

        assertThat(result.values).containsExactly(reservedSeat)
    }

    @Test
    fun `영화 제목과 시작 시간이 같으면 같은 상영으로 판단한다`() {
        val start = LocalDateTime.of(2026, 5, 1, 10, 0)
        val screening1 = screening(startTime = start)
        val screening2 = screening(startTime = start)

        assertThat(screening1.isSameScreening(screening2)).isTrue()
    }

    @Test
    fun `영화 제목이 같으면 같은 영화로 판단한다`() {
        val screening = screening(startTime = LocalDateTime.of(2026, 5, 1, 10, 0))

        assertThat(screening.isSameMovie("인터스텔라")).isTrue()
        assertThat(screening.isSameMovie("포비의 반란군")).isFalse()
    }

    @Test
    fun `상영 날짜가 같으면 true를 반환한다`() {
        val screening = screening(startTime = LocalDateTime.of(2026, 5, 1, 10, 0))

        assertThat(screening.isSameDate(LocalDate.of(2026, 5, 1))).isTrue()
        assertThat(screening.isSameDate(LocalDate.of(2026, 5, 2))).isFalse()
    }

    @Test
    fun `종료 시각은 시작 시각에 러닝타임을 더한 값이다`() {
        val screening =
            screening(
                startTime = LocalDateTime.of(2026, 5, 1, 10, 0),
                runningTime = 130,
            )

        assertThat(screening.endTime()).isEqualTo(LocalDateTime.of(2026, 5, 1, 12, 10))
    }

    @Test
    fun `상영 시간이 겹치면 true를 반환한다`() {
        val screening1 = screening(startTime = LocalDateTime.of(2026, 5, 1, 10, 0), runningTime = 120)
        val screening2 = screening(startTime = LocalDateTime.of(2026, 5, 1, 11, 0), runningTime = 120)

        assertThat(screening1.overlaps(screening2)).isTrue()
    }

    @Test
    fun `이전 영화의 끝 시각과 다음 영화의 시작 시각이 같더라도 겹침 판단하지 않는다`() {
        val screening1 = screening(startTime = LocalDateTime.of(2026, 5, 1, 10, 0), runningTime = 120)
        val screening2 = screening(startTime = LocalDateTime.of(2026, 5, 1, 12, 0), runningTime = 120)

        assertThat(screening1.overlaps(screening2)).isFalse()
    }
}

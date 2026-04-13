package movie.domain.reservation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SeatTest {
    private val column = SeatColumn(1)

    @Test
    fun `좌석은 행과 열로 구성된다`() {
        // given
        val row = SeatRow("A")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.row).isEqualTo(row)
        assertThat(seat.column).isEqualTo(column)
        assertThat(seat.grade).isEqualTo(SeatGrade.B)
    }

    @Test
    fun `A열 좌석은 B등급 좌석이다`() {
        // given
        val row = SeatRow("A")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.grade).isEqualTo(SeatGrade.B)
    }

    @Test
    fun `B열 좌석은 B등급 좌석이다`() {
        // given
        val row = SeatRow("B")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.grade).isEqualTo(SeatGrade.B)
    }

    @Test
    fun `C열 좌석은 S등급 좌석이다`() {
        // given
        val row = SeatRow("C")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.grade).isEqualTo(SeatGrade.S)
    }

    @Test
    fun `D열 좌석은 S등급 좌석이다`() {
        // given
        val row = SeatRow("D")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.grade).isEqualTo(SeatGrade.S)
    }

    @Test
    fun `E열 좌석은 A등급 좌석 가격이다`() {
        // given
        val row = SeatRow("E")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.grade).isEqualTo(SeatGrade.A)
    }

    @Test
    fun `좌석은 행과 열이 합쳐진 좌석 번호를 가진다`() {
        // given
        val row = SeatRow("A")

        // when
        val seat = Seat(row, column)

        // then
        assertThat(seat.seatNumber).isEqualTo("A1")
    }
}

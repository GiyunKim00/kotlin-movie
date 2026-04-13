package movie.domain.reservation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SeatsTest {
    val seats = Seats.create()

    @Test
    fun `존재하는 좌석이라면 해당 좌석을 반환한다`() {
        // given
        val findingSeats = listOf("A1", "B1")

        // when
        val foundSeat = seats.findAllBySeatNumbers(findingSeats)

        // then
        assertThat(foundSeat.seats[0].seatNumber).isEqualTo(findingSeats[0])
        assertThat(foundSeat.seats[1].seatNumber).isEqualTo(findingSeats[1])
    }

    @Test
    fun `좌석이 존재하지 않는다면 IllegalArgumentException을 던진다`() {
        // given
        val findingSeats = listOf("A5")

        // when & then
        assertThrows<IllegalArgumentException> { seats.findAllBySeatNumbers(findingSeats) }
    }
}

package movie.domain.reservation

import movie.view.toDisplayText
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
        assertThat(foundSeat.values[0].toDisplayText()).isEqualTo(findingSeats[0])
        assertThat(foundSeat.values[1].toDisplayText()).isEqualTo(findingSeats[1])
    }

    @Test
    fun `좌석이 존재하지 않는다면 IllegalArgumentException을 던진다`() {
        // given
        val findingSeats = listOf("A5")

        // when & then
        assertThrows<IllegalArgumentException> { seats.findAllBySeatNumbers(findingSeats) }
    }
}

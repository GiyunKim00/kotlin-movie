package movie.domain.reservation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SeatsTest {
    val seats = Seats.create()

    @Test
    fun `존재하는 좌석이라면 해당 좌석을 반환한다`() {
        val findingSeats = listOf("A1", "B1")

        val foundSeat = seats.findAllBySeatNumbers(findingSeats)

        assertEquals(findingSeats[0], foundSeat.seats[0].seatNumber)
        assertEquals(findingSeats[1], foundSeat.seats[1].seatNumber)
    }

    @Test
    fun `좌석이 존재하지 않는다면 IllegalArgumentException을 던진다`() {
        val findingSeats = listOf("A5")

        assertThrows<IllegalArgumentException> { seats.findAllBySeatNumbers(findingSeats) }
    }
}

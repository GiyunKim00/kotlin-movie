package movie.service

import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.repository.ScreeningRepository
import java.time.LocalDate

class ReservationService(
    private val repository: ScreeningRepository,
    private val allSeats: Seats,
) {
    fun findAvailableScreenings(
        title: String,
        date: LocalDate,
    ): List<Screening> {
        val screenings = repository.findByMovieTitleAndDate(title, date)
        require(screenings.isNotEmpty()) { "해당 조건의 상영이 없습니다." }
        return screenings
    }

    fun reservedSeats(screening: Screening): Seats {
        val sameScreen = repository.findSameScreening(screening) ?: screening

        return sameScreen.reservedSeatsFrom(allSeats)
    }

    fun reserve(
        cart: Cart,
        screening: Screening,
        seatNumbers: List<String>,
    ): ReservationResult {
        val sameScreen =
            repository.findSameScreening(screening)
                ?: throw IllegalArgumentException("존재하지 않는 상영입니다.")

        val selectedSeats = allSeats.findAllBySeatNumbers(seatNumbers)

        require(!sameScreen.hasReservedSeat(selectedSeats)) {
            "이미 예약된 좌석은 다시 선택할 수 없습니다."
        }

        val reservedItem = ReservedScreen(sameScreen, selectedSeats)
        val updatedCart = cart.add(reservedItem)

        return ReservationResult(
            reservedScreen = reservedItem,
            updatedCart = updatedCart,
        )
    }
}

data class ReservationResult(
    val reservedScreen: ReservedScreen,
    val updatedCart: Cart,
)

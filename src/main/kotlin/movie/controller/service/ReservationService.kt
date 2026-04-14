package movie.controller.service

import movie.controller.parser.SeatParser
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.repository.CinemaRepository
import java.time.LocalDate

class ReservationService(
    private val repository: CinemaRepository,
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

    fun validateScreeningOverlap(
        cart: Cart,
        target: Screening,
    ) {
        cart.reservedScreens.forEach {
            require(!it.screen.overlaps(target)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }
        }
    }

    fun reservedSeats(screening: Screening): Seats {
        val sameScreen =
            repository.screenings.firstOrNull {
                it.movie == screening.movie && it.startTime == screening.startTime
            } ?: screening

        return sameScreen.reservedSeatsFrom(allSeats)
    }

    fun parseSeatNumbers(rawInput: String): List<String> {
        require(rawInput.isNotBlank()) { "올바른 좌석 번호를 입력해주세요." }

        val seatNumbers = SeatParser.parse(rawInput)

        require(seatNumbers.toSet().size == seatNumbers.size) {
            "동일 좌석을 중복 예약할 수 없습니다."
        }

        return seatNumbers
    }

    fun reserve(
        cart: Cart,
        screening: Screening,
        seatNumbers: List<String>,
    ): ReservationResult {
        val selectedSeats = allSeats.findAllBySeatNumbers(seatNumbers)

        require(!screening.hasReservedSeat(selectedSeats)) {
            "이미 예약된 좌석은 다시 선택할 수 없습니다."
        }

        val reservedItem = ReservedScreen(screening, selectedSeats)
        val updatedCart = cart.add(reservedItem)

        updateScreeningReservation(screening, selectedSeats)

        return ReservationResult(
            reservedScreen = reservedItem,
            updatedCart = updatedCart,
        )
    }

    private fun updateScreeningReservation(
        screening: Screening,
        selectedSeats: Seats,
    ) {
        repository.updateScreening(
            repository.screenings.map {
                if (it.movie == screening.movie && it.startTime == screening.startTime) {
                    it.reserve(selectedSeats.values)
                } else it
            },
        )
    }
}

data class ReservationResult(
    val reservedScreen: ReservedScreen,
    val updatedCart: Cart,
)
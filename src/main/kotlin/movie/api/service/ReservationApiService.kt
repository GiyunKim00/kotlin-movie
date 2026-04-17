package movie.api.service

import movie.api.dto.ReservationRequest
import movie.api.dto.ReservationResponse
import movie.api.dto.ReservedItem
import movie.domain.account.Account
import movie.domain.payment.PayResult
import movie.domain.payment.PaymentMethod
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seats
import movie.repository.ScreeningRepository
import movie.view.toDisplayText
import org.springframework.stereotype.Service

@Service
class ReservationApiService(
    private val screeningRepository: ScreeningRepository,
) {
    private val account = Account()
    private val allSeats = Seats.create()

    fun createReservation(request: ReservationRequest): ReservationResponse {
        var cart = Cart()

        request.reservations.forEach { item ->
            val screening =
                screeningRepository.findScreeningById(item.screeningId)
                    ?: throw IllegalArgumentException("존재하지 않는 상영입니다.")

            val selectedSeats = allSeats.findAllBySeatNumbers(item.seats)

            require(!screening.hasReservedSeat(selectedSeats)) {
                "이미 예약된 좌석은 다시 선택할 수 없습니다."
            }

            val reservedScreen = ReservedScreen(screening, selectedSeats)
            cart = cart.add(reservedScreen)
        }

        val paymentMethod = PaymentMethod.valueOf(request.paymentMethod)
        val payment = movie.controller.service.PaymentService(account)
        when (val result = payment.pay(cart, request.usedPoints, paymentMethod)) {
            is PayResult.Failure -> throw IllegalArgumentException(result.message)
            is PayResult.Success -> {
                result.cart.reservedScreens.forEach { reserved ->
                    screeningRepository.reserveSeats(
                        screening = reserved.screen,
                        selectedSeats = reserved.seats,
                    )
                }

                return ReservationResponse(
                    reservationId = 1L,
                    reservations =
                        result.cart.reservedScreens.map { reserved ->
                            ReservedItem(
                                screeningId = requireNotNull(reserved.screen.id),
                                seats = reserved.seats.values.map { it.toDisplayText() },
                            )
                        },
                    usedPoints = result.usedPoint,
                    paymentMethod = result.paymentMethod.name,
                    totalPrice = result.paidAmount.amount,
                )
            }
        }
    }
}

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
    fun createReservation(request: ReservationRequest): ReservationResponse {
        val cart = buildCart(request)

        val paymentMethod = parsePaymentMethod(request.paymentMethod)
        val payment = createPaymentService()

        return when(val result = payment.pay(cart, request.usedPoints, paymentMethod)) {
            is PayResult.Success -> {
                reserveSeats(result.cart)
                toReservationResponse(result)
            }
            is PayResult.Failure -> throw IllegalArgumentException(result.message)
        }
    }

    private fun buildCart(request: ReservationRequest): Cart {
        val allSeats = Seats.create()

        return request.reservations.fold(Cart()) { cart, item ->
            val screening = findScreening(item.screeningId)
            val selectedSeats = allSeats.findAllBySeatNumbers(item.seats)
            validateNotReserved(screening, selectedSeats)

            cart.add(
                ReservedScreen(
                    screen = screening,
                    seats = selectedSeats,
                ),
            )
        }
    }

    private fun findScreening(screeningId: Long) =
        screeningRepository.findScreeningById(screeningId)
            ?: throw IllegalArgumentException("존재하지 않는 상영입니다.")

    private fun validateNotReserved(
        screening: movie.domain.screening.Screening,
        selectedSeats: Seats,
    ) {
        require(!screening.hasReservedSeat(selectedSeats)) {
            "이미 예약된 좌석은 다시 선택할 수 없습니다."
        }
    }

    private fun parsePaymentMethod(rawPaymentMethod: String): PaymentMethod =
        runCatching { PaymentMethod.valueOf(rawPaymentMethod) }
            .getOrElse { throw IllegalArgumentException("지원하지 않는 결제 수단입니다.") }

    private fun createPaymentService(): PaymentService {
        val account = Account()
        return PaymentService(account)
    }

    private fun reserveSeats(cart: Cart) {
        cart.reservedScreens.forEach { reserved ->
            screeningRepository.reserveSeats(
                screening = reserved.screen,
                selectedSeats = reserved.seats,
            )
        }
    }

    private fun toReservationResponse(result: PayResult.Success): ReservationResponse =
        ReservationResponse(
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

package movie.api.dto

import movie.domain.payment.PaymentMethod

data class ReservationRequest(
    val reservations: List<ReservedItem>,
    val usedPoints: Int,
    val paymentMethod: PaymentMethod,
)

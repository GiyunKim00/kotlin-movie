package movie.api.dto

import movie.domain.payment.PaymentMethod

data class ReservationResponse(
    val reservationId: Long,
    val reservations: List<ReservedItem>,
    val usedPoints: Int,
    val paymentMethod: PaymentMethod,
    val totalPrice: Int,
)

data class ReservedItem(
    val screeningId: Long,
    val seats: List<String>,
)

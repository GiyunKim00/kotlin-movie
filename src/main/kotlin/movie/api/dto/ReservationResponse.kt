package movie.api.dto

data class ReservationResponse(
    val reservationId: Long,
    val reservations: List<ReservedItem>,
    val usedPoints: Int,
    val paymentMethod: String,
    val totalPrice: Int,
)

data class ReservedItem(
    val screeningId: Long,
    val seats: List<String>,
)

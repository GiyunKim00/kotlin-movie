package movie.api.dto

data class ReservationRequest(
    val reservations: List<ReservedItem>,
    val usedPoints: Int,
    val paymentMethod: String,
)

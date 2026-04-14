package movie.domain.reservation

import movie.domain.payment.Money
import movie.domain.screening.Screening

class ReservedScreen(
    val screen: Screening,
    val seats: Seats,
) {
    fun price(): Money = Money(seats.totalPrice())
}

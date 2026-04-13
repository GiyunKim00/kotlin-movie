package movie.domain.reservation

import movie.domain.screening.Screening

class ReservedScreen(
    val screen: Screening,
    val seats: Seats,
) {
    fun price(): Int = seats.seats.sumOf { it.grade.money }
}

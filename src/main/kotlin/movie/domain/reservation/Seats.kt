package movie.domain.reservation

class Seats private constructor(
    val seats: List<Seat>,
) {
    companion object {
        fun create(policy: SeatsPolicy = SeatsPolicy()): Seats {
            val seats = policy.rowRange().flatMap { rowIndex ->
                policy.columnRange().map { columnIndex ->
                    policy.createSeat(rowIndex, columnIndex)
                }
            }
            return Seats(seats)
        }
        fun create(seats: List<Seat>): Seats = Seats(seats)
    }

    private fun findBySeatNumber(seatNumber: String): Seat =
        seats.firstOrNull { it.seatNumber == seatNumber }
            ?: throw IllegalArgumentException("유효하지 않은 좌석 번호입니다.")

    fun findAllBySeatNumbers(seatNumbers: List<String>): Seats =
        Seats(seatNumbers.map { findBySeatNumber(it) })
}

package movie.domain.reservation

class Seats(
    val values: List<Seat>,
) {
    companion object {
        fun create(policy: SeatsPolicy = SeatsPolicy()): Seats {
            val seats =
                policy.rowRange().flatMap { rowIndex ->
                    policy.columnRange().map { columnIndex ->
                        policy.createSeat(rowIndex, columnIndex)
                    }
                }
            return Seats(seats)
        }
    }

    fun totalPrice(): Int = values.sumOf { it.price() }

    fun any(predicate: (Seat) -> Boolean): Boolean = values.any(predicate)

    fun none(predicate: (Seat) -> Boolean): Boolean = values.none(predicate)

    fun filter(predicate: (Seat) -> Boolean): Seats = Seats(values.filter(predicate))

    fun contains(seat: Seat): Boolean = values.contains(seat)

    fun groupByRow(): Map<SeatRow, Seats> =
        values
            .groupBy { it.row }
            .mapValues { (_, seats) -> Seats(seats) }

    fun findBySeatNumber(seatNumber: String): Seat =
        values.firstOrNull { it.matches(seatNumber) }
            ?: throw IllegalArgumentException("유효하지 않은 좌석 번호입니다.")

    fun findAllBySeatNumbers(seatNumbers: List<String>): Seats = Seats(seatNumbers.map { findBySeatNumber(it) })
}

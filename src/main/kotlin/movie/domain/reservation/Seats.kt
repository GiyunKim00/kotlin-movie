package movie.domain.reservation

class Seats private constructor(
    private val values: List<Seat>,
) {
    init {
        require(values.size == TOTAL_SEAT_COUNT) { "좌석은 총 ${TOTAL_SEAT_COUNT}개여야 합니다." }
    }

    companion object {
        private const val ROW_SIZE = 5
        private const val COL_SIZE = 4
        private const val TOTAL_SEAT_COUNT = ROW_SIZE * COL_SIZE

        fun create(): Seats {
            val seats = mutableListOf<Seat>()

            for (rowIndex in 0 until ROW_SIZE) {
                for (columnIndex in 0 until COL_SIZE) {
                    seats.add(Seat.create(rowIndex, columnIndex))
                }
            }

            return Seats(seats)
        }
    }

    fun allSeats(): List<Seat> = values.toList()

    private fun findBySeatNumber(seatNumber: String): Seat =
        values.firstOrNull { it.seatNumber == seatNumber.trim().uppercase() }
            ?: throw IllegalArgumentException("존재하지 않는 좌석입니다: $seatNumber")

    fun findAllBySeatNumbers(seatNumbers: List<String>): List<Seat> = seatNumbers.map { findBySeatNumber(it) }
}

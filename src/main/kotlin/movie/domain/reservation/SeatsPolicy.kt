package movie.domain.reservation

class SeatsPolicy(
    private val rowSize: Int = 5,
    private val columnSize: Int = 4,
) {
    fun createSeat(rowIndex: Int, columnIndex: Int): Seat {
        validateRowIndex(rowIndex)
        validateColumnIndex(columnIndex)

        return Seat(
            row = SeatRow.setRow(rowIndex),
            column = SeatColumn.setCol(columnIndex),
        )
    }
    fun rowRange(): IntRange = 0 until rowSize

    fun columnRange(): IntRange = 0 until columnSize

    private fun validateRowIndex(rowIndex: Int) {
        require(rowIndex in 0 until rowSize) { "유효하지 않은 행 인덱스입니다." }
    }

    private fun validateColumnIndex(columnIndex: Int) {
        require(columnIndex in 0 until columnSize) { "유효하지 않은 열 인덱스입니다." }
    }
}
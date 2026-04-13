package movie.domain.reservation

data class Seat(
    val row: SeatRow,
    val column: SeatColumn,
) {
    val seatNumber: String = "${row.value}${column.value}"

    val grade: SeatGrade = SeatGrade.grantGrade(row)
}

@JvmInline
value class SeatRow(
    val value: String,
) {
    companion object {
        fun setRow(rowIndex: Int): SeatRow = SeatRow(('A' + rowIndex).toString())
    }
}

@JvmInline
value class SeatColumn(
    val value: Int,
) {
    companion object {
        fun setCol(columnIndex: Int): SeatColumn = SeatColumn(columnIndex + 1)
    }
}

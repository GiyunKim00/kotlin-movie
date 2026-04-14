package movie.domain.reservation

data class Seat(
    val row: SeatRow,
    val column: SeatColumn,
) {
    val grade: SeatGrade = SeatGrade.grantGrade(row)

    fun price(): Int = grade.money.amount

    fun matches(seatNumber: String): Boolean = row.value + column.value.toString() == seatNumber
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

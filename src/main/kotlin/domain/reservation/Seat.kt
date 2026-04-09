package domain.reservation

data class Seat(
    val row: SeatRow,
    val column: SeatColumn,
    val grade: SeatGrade
) {
    val seatNumber: String by lazy { "${row.value}${column.value}" }

    companion object {
        fun create(rowIndex: Int, columnIndex: Int): Seat {
            val row = SeatRow.setRow(rowIndex)
            val column = SeatColumn.setCol(columnIndex)
            val grade = SeatGrade.grantGrade(rowIndex, columnIndex)

            return Seat(
                row = row,
                column = column,
                grade = grade,
            )
        }
    }
}

@JvmInline
value class SeatRow(val value: String) {
    companion object {
        fun setRow(rowIndex: Int): SeatRow {
            require(rowIndex in 0..4) { "유효하지 않은 행 인덱스입니다." }
            return SeatRow(('A' + rowIndex).toString())
        }
    }
}

@JvmInline
value class SeatColumn(val value: Int) {
    companion object {
        fun setCol(columnIndex: Int): SeatColumn {
            require(columnIndex in 0..3) { "유효하지 않은 열 인덱스입니다." }
            return SeatColumn(columnIndex + 1)
        }
    }
}

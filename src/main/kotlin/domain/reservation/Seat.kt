package domain.reservation

class Seat(
    val row: SeatRow,
    val column: SeatColumn,
    val grade: SeatGrade
) {
    val seatNumber: String by lazy { "${row.value}${column.value}" }
}

@JvmInline
value class SeatRow(val value: String)

@JvmInline
value class SeatColumn(val value: Int)

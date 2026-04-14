package movie.view

import movie.domain.reservation.Seat
import movie.domain.reservation.Seats

fun Seat.toDisplayText(): String = "${row.value}${column.value}"

fun Seats.toDisplayText(): String = values.joinToString(", ") { it.toDisplayText() }
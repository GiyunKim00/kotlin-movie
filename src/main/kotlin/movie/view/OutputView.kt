package movie.view

import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import java.time.format.DateTimeFormatter

class OutputView {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun printStartMessage() {
        println("영화 예매를 시작합니다. 새 예매를 생성하시겠습니까? (Y/N)")
    }

    fun printScreenings(screenings: List<Screening>) {
        println("해당 날짜의 상영 목록")
        screenings.forEachIndexed { idx, screening ->
            println("[${idx + 1}] ${screening.startTime.value.toLocalTime()}")
        }
    }

    fun printSeatLayout(
        allSeats: Seats,
        reservedSeats: Seats,
    ) {
        println("좌석 배치도")
        println("    1    2    3    4")

        allSeats.groupByRow().forEach { (row, seatsInRow) ->
            val seatText =
                seatsInRow.values.joinToString(" ") { seat ->
                    if (reservedSeats.contains(seat)) "[ X]" else "[ ${seat.grade.name}]"
                }
            println("${row.value} $seatText")
        }
    }

    fun printCartAdded(item: ReservedScreen) {
        println("장바구니에 추가됨")
        println(
            "- [${item.screen.movie.title.value}] " +
                    "${item.screen.startTime.value.format(dateTimeFormatter)}  " +
                    "좌석: ${item.seats.toDisplayText()}",
        )
    }

    fun printCart(cart: Cart) {
        println("장바구니")
        cart.reservedScreens.forEach {
            println(
                "- [${it.screen.movie.title.value}] " +
                        "${it.screen.startTime.value.format(dateTimeFormatter)}  " +
                        "좌석: ${it.seats.toDisplayText()}",
            )
        }
    }

    fun printMessage(message: String) {
        println(message)
    }
}

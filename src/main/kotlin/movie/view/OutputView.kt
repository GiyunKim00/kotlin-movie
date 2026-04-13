package movie.view

import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seat
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import java.time.format.DateTimeFormatter

class OutputView {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
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
        reservedSeats: List<Seat>,
    ) {
        println("좌석 배치도")
        println("    1    2    3    4")

        val grouped = allSeats.allSeats().groupBy { it.row.value }
        grouped.forEach { (row, seats) ->
            val seatText =
                seats.joinToString(" ") { seat ->
                    if (reservedSeats.contains(seat)) {
                        "[ X]"
                    } else {
                        "[ ${seat.grade.name}]"
                    }
                }
            println("$row $seatText")
        }
    }

    fun printCartAdded(item: ReservedScreen) {
        val seats = item.seats.joinToString(", ") { it.seatNumber }
        println("장바구니에 추가됨")
        println(
            "- [${item.screen.movie.title.value}] ${
                item.screen.startTime.value.format(dateTimeFormatter)
            }  좌석: $seats",
        )
    }

    fun printCart(cart: Cart) {
        println("장바구니")
        cart.items.forEach {
            val seats = it.seats.joinToString(", ") { seat -> seat.seatNumber }
            println(
                "- [${it.screen.movie.title.value}] ${
                    it.screen.startTime.value.format(dateTimeFormatter)
                }  좌석: $seats",
            )
        }
    }

    fun printPaymentResult(
        amount: Int,
        usedPoint: Int,
    ) {
        println("예매 완료")
        println("내역:")
        println("결제 금액: ${"%,d".format(amount)}원  (포인트 ${"%,d".format(usedPoint)}원 사용)")
        println()
        println("감사합니다.")
    }

    fun printMessage(message: String) {
        println(message)
    }
}

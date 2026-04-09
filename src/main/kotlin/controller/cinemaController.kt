package controller

import domain.account.Account
import domain.payment.DiscountPolicy
import domain.payment.PayResult
import domain.payment.Payment
import domain.payment.PaymentMethod
import domain.reservation.Cart
import domain.reservation.ReservedScreen
import domain.reservation.Seat
import domain.reservation.Seats
import domain.screening.Screening
import repository.CinemaRepository
import view.InputView
import view.OutputView
import java.time.LocalDate

class CinemaController(
    private val repository: CinemaRepository,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val account: Account = Account(),
    private val allSeats: Seats = Seats.create(),
) {
    private var cart: Cart = Cart()

    fun run() {
        outputView.printStartMessage()
        val start = inputView.readYesOrNo("").uppercase()
        if (start != "Y") {
            outputView.printMessage("예매를 종료합니다.")
            return
        }

        do {
            reserveOneMovie()
        } while (inputView.readYesOrNo("다른 영화를 추가하시겠습니까? (Y/N)").uppercase() == "Y")

        outputView.printCart(cart)

        val point = inputView.readPointAmount()
        val paymentMethod = readPaymentMethod()
        val payment = Payment(cart, DiscountPolicy())

        outputView.printMessage("가격 계산")
        when (val result = payment.pay(point, account, paymentMethod)) {
            is PayResult.Success -> {
                outputView.printMessage("최종 결제 금액: ${"%,d".format(result.paidAmount)}원")
                val confirm = inputView.readYesOrNo("위 금액으로 결제하시겠습니까? (Y/N)").uppercase()
                if (confirm == "Y") {
                    printReservationHistory(result)
                } else {
                    outputView.printMessage("결제가 취소되었습니다.")
                }
            }
            is PayResult.Failure -> outputView.printMessage(result.message)
        }
    }

    private fun reserveOneMovie() {
        val title = inputView.readMovieTitle()
        val date = LocalDate.parse(inputView.readDate())

        val availableScreenings = repository.findByMovieTitleAndDate(title, date)
        require(availableScreenings.isNotEmpty()) { "해당 조건의 상영이 없습니다." }

        var selectedScreening: Screening
        while (true) {
            outputView.printScreenings(availableScreenings)
            val selectedNumber = inputView.readScreeningNumber()
            selectedScreening = availableScreenings[selectedNumber - 1]

            try {
                validateScreeningOverlap(selectedScreening)
                break
            } catch (e: IllegalArgumentException) {
                outputView.printMessage(e.message ?: "상영 선택 중 오류가 발생했습니다.")
            }
        }

        outputView.printSeatLayout(allSeats, selectedScreeningReservedSeats(selectedScreening))

        val selectedSeats = readSeats()
        validateSeatsNotReserved(selectedScreening, selectedSeats)

        val reservedItem = ReservedScreen(
            screen = selectedScreening,
            seats = selectedSeats,
        )

        cart = cart.add(reservedItem)
        updateScreeningReservation(selectedScreening, selectedSeats)

        outputView.printCartAdded(reservedItem)
    }

    private fun validateScreeningOverlap(target: Screening) {
        cart.items.forEach {
            require(!it.screen.overlaps(target)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }
        }
    }

    private fun readSeats(): List<Seat> {
        val input = inputView.readSeatNumbers()
        val seatNumbers = input.split(",")
            .map { it.trim().uppercase() }
            .filter { it.isNotBlank() }

        return allSeats.findAllBySeatNumbers(seatNumbers)
    }

    private fun validateSeatsNotReserved(screening: Screening, seats: List<Seat>) {
        require(seats.none { screening.isReserved(it) }) {
            "이미 예약된 좌석은 다시 선택할 수 없습니다."
        }
    }

    private fun readPaymentMethod(): PaymentMethod {
        return when (inputView.readPaymentMethod()) {
            1 -> PaymentMethod.CREDIT_CARD
            2 -> PaymentMethod.CASH
            else -> throw IllegalArgumentException("올바른 결제 수단을 선택해 주세요.")
        }
    }

    private fun printReservationHistory(result: PayResult.Success) {
        outputView.printMessage("예매 완료")
        outputView.printMessage("내역:")
        result.cart.items.forEach {
            val seats = it.seats.joinToString(", ") { seat -> seat.seatNumber }
            outputView.printMessage(
                "- [${it.screen.movie.title.value}] ${it.screen.startTime.value.toLocalDate()} ${it.screen.startTime.value.toLocalTime()}  좌석: $seats"
            )
        }
        outputView.printMessage(
            "결제 금액: ${"%,d".format(result.paidAmount)}원  (포인트 ${"%,d".format(result.usedPoint)}원 사용)"
        )
        outputView.printMessage("")
        outputView.printMessage("감사합니다.")
    }

    private fun selectedScreeningReservedSeats(screening: Screening): List<Seat> {
        val sameScreen = repository.screenings.firstOrNull {
            it.movie == screening.movie && it.startTime == screening.startTime
        } ?: screening

        return allSeats.allSeats().filter { sameScreen.isReserved(it) }
    }

    private fun updateScreeningReservation(screening: Screening, selectedSeats: List<Seat>) {
        repository.updateScreening(
            repository.screenings.map {
                if (it.movie == screening.movie && it.startTime == screening.startTime) {
                    it.reserve(selectedSeats)
                } else {
                    it
                }
            }
        )
    }
}
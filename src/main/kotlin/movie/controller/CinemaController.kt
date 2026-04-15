package movie.controller

import movie.controller.parser.DateParser
import movie.controller.parser.SeatParser
import movie.controller.service.PaymentService
import movie.controller.service.ReservationService
import movie.domain.account.Account
import movie.domain.payment.PayResult
import movie.domain.reservation.Cart
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.repository.CinemaRepository
import movie.view.InputView
import movie.view.OutputView

class CinemaController(
    private val repository: CinemaRepository,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val account: Account = Account(),
    private val allSeats: Seats = Seats.create(),
    private var cart: Cart = Cart(),
) {
    private val reservationService = ReservationService(repository, allSeats)
    private val paymentService = PaymentService(account)

    fun run() {
        outputView.printStartMessage()

        if (!isReservationStarted()) {
            outputView.printMessage("예매를 종료합니다.")
            return
        }

        reserveMovies()
        proceedPayment()
    }

    private fun isReservationStarted(): Boolean = inputView.readYesOrNo("").uppercase() == "Y"

    private fun reserveMovies() {
        do {
            retryPrompt { reserveOneMovie() }
        } while (askToAddMoreMovie())
    }

    private fun askToAddMoreMovie(): Boolean = inputView.readYesOrNo("다른 영화를 추가하시겠습니까? (Y/N)").uppercase() == "Y"

    private fun reserveOneMovie() {
        val title = inputView.readMovieTitle()
        val date = DateParser.parse(inputView.readDate())
        val availableScreenings = reservationService.findAvailableScreenings(title, date)
        val selectedScreening = readAvailableScreening(availableScreenings)
        val reservedSeats = reservationService.reservedSeats(selectedScreening)

        outputView.printSeatLayout(allSeats, reservedSeats)

        val seatNumbers =
            retryPrompt {
                parseSeatNumbers(inputView.readSeatNumbers())
            }

        val result =
            retryPrompt {
                reservationService.reserve(cart, selectedScreening, seatNumbers)
            }

        cart = result.updatedCart
        outputView.printCartAdded(result.reservedScreen)
    }

    private fun readAvailableScreening(availableScreenings: List<Screening>): Screening =
        retryPrompt {
            outputView.printScreenings(availableScreenings)

            val selectedNumber = inputView.readScreeningNumber()
            require(selectedNumber in 1..availableScreenings.size) {
                "올바른 상영 번호를 선택해 주세요."
            }

            val selectedScreening = availableScreenings[selectedNumber - 1]
            cart.validateOverlap(selectedScreening)
            selectedScreening
        }

    private fun parseSeatNumbers(rawInput: String): List<String> {
        require(rawInput.isNotBlank()) { "올바른 좌석 번호를 입력해주세요." }

        val seatNumbers = SeatParser.parse(rawInput)

        require(seatNumbers.toSet().size == seatNumbers.size) {
            "동일 좌석을 중복 예약할 수 없습니다."
        }

        return seatNumbers
    }

    private fun proceedPayment() {
        outputView.printCart(cart)

        val point =
            retryPrompt {
                paymentService.validatePoint(inputView.readPointAmount())
            }
        val paymentMethod =
            retryPrompt {
                paymentService.validatePaymentMethod(inputView.readPaymentMethod())
            }

        outputView.printMessage("가격 계산")

        when (val result = paymentService.pay(cart, point, paymentMethod)) {
            is PayResult.Success -> confirmPayment(result)
            is PayResult.Failure -> outputView.printMessage(result.message)
        }
    }

    private fun confirmPayment(result: PayResult.Success) {
        outputView.printFinalAmount(result)

        val confirm = inputView.readYesOrNo("위 금액으로 결제하시겠습니까? (Y/N)").uppercase()
        if (confirm == "Y") {
            outputView.printReservationHistory(result)
            return
        }

        outputView.printMessage("결제가 취소되었습니다.")
    }

    private fun <T> retryPrompt(action: () -> T): T {
        while (true) {
            try {
                return action()
            } catch (e: Exception) {
                outputView.printMessage(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}

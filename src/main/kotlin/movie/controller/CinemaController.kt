package movie.controller

import movie.domain.account.Account
import movie.domain.payment.DiscountPolicy
import movie.domain.payment.MovieDayDiscountMethod
import movie.domain.payment.PayResult
import movie.domain.payment.Payment
import movie.domain.payment.PaymentMethod
import movie.domain.payment.TimeSaleDiscountMethod
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.repository.CinemaRepository
import movie.view.InputView
import movie.view.OutputView
import movie.view.toDisplayText
import java.time.LocalDate

class CinemaController(
    private val repository: CinemaRepository,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val account: Account = Account(),
    private val allSeats: Seats = Seats.create(),
    private var cart: Cart = Cart()
) {
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

    private fun askToAddMoreMovie(): Boolean =
        inputView.readYesOrNo("다른 영화를 추가하시겠습니까? (Y/N)").uppercase() == "Y"

    private fun reserveOneMovie() {
        val title = inputView.readMovieTitle()
        val date = readDate()
        val availableScreenings = findAvailableScreenings(title, date)
        val selectedScreening = readAvailableScreening(availableScreenings)
        val selectedSeats = retryPrompt { readAvailableSeats(selectedScreening) }

        val reservedItem =
            ReservedScreen(
                screen = selectedScreening,
                seats = selectedSeats,
            )

        addToCart(reservedItem)
        outputView.printCartAdded(reservedItem)
    }

    private fun readDate(): LocalDate =
        try {
            LocalDate.parse(inputView.readDate())
        } catch (e: Exception) {
            throw IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)")
        }

    private fun findAvailableScreenings(
        title: String,
        date: LocalDate,
    ): List<Screening> {
        val screenings = repository.findByMovieTitleAndDate(title, date)
        require(screenings.isNotEmpty()) { "해당 조건의 상영이 없습니다." }
        return screenings
    }

    private fun readAvailableScreening(availableScreenings: List<Screening>): Screening =
        retryPrompt {
            outputView.printScreenings(availableScreenings)

            val selectedNumber = inputView.readScreeningNumber()
            require(selectedNumber in 1..availableScreenings.size) {
                "올바른 상영 번호를 선택해 주세요."
            }

            val selectedScreening = availableScreenings[selectedNumber - 1]
            validateScreeningOverlap(selectedScreening)
            selectedScreening
        }

    private fun readAvailableSeats(screening: Screening): Seats {
        outputView.printSeatLayout(allSeats, selectedScreeningReservedSeats(screening))

        val selectedSeats = readSeats()
        validateSeatsNotReserved(screening, selectedSeats)
        return selectedSeats
    }

    private fun addToCart(reservedItem: ReservedScreen) {
        cart = cart.add(reservedItem)
        updateScreeningReservation(reservedItem.screen, reservedItem.seats)
    }

    private fun proceedPayment() {
        outputView.printCart(cart)

        val discountPolicy = DiscountPolicy(
            listOf(
                MovieDayDiscountMethod,
                TimeSaleDiscountMethod,
            ),
        )

        val payment = Payment(
            cart = cart,
            discountPolicy = discountPolicy,
        )

        val point =
            retryPrompt {
                val inputPoint = inputView.readPointAmount()
                require(account.point.amount >= inputPoint) {
                    "보유 포인트가 부족합니다."
                }
                inputPoint
            }
        val paymentMethod = retryPrompt { PaymentMethod.validate(inputView.readPaymentMethod()) }

        outputView.printMessage("가격 계산")

        when (val result = payment.pay(point, account, paymentMethod)) {
            is PayResult.Success -> confirmPayment(result)
            is PayResult.Failure -> outputView.printMessage(result.message)
        }
    }

    private fun confirmPayment(result: PayResult.Success) {
        outputView.printMessage("최종 결제 금액: ${"%,d".format(result.paidAmount.amount)}원")

        val confirm = inputView.readYesOrNo("위 금액으로 결제하시겠습니까? (Y/N)").uppercase()
        if (confirm == "Y") {
            printReservationHistory(result)
            return
        }

        outputView.printMessage("결제가 취소되었습니다.")
    }

    private fun validateScreeningOverlap(target: Screening) {
        cart.reservedScreens.forEach {
            require(!it.screen.overlaps(target)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }
        }
    }

    private fun readSeats(): Seats {
        val input = inputView.readSeatNumbers()
        require(input.isNotBlank()) { "올바른 좌석 번호를 입력해주세요." }

        val seatNumbers =
            input
                .split(",")
                .map { it.trim().uppercase() }
                .filter { it.isNotBlank() }

        require(seatNumbers.toSet().size == seatNumbers.size) {
            "동일 좌석을 중복 예약할 수 없습니다."
        }

        return allSeats.findAllBySeatNumbers(seatNumbers)
    }

    private fun validateSeatsNotReserved(
        screening: Screening,
        seats: Seats,
    ) {
        require(!screening.hasReservedSeat(seats)) {
            "이미 예약된 좌석은 다시 선택할 수 없습니다."
        }
    }

    private fun printReservationHistory(result: PayResult.Success) {
        outputView.printMessage("예매 완료")
        outputView.printMessage("내역:")

        result.cart.reservedScreens.forEach {
            outputView.printMessage(
                "- [${it.screen.movie.title.value}] " +
                        "${it.screen.startTime.value.toLocalDate()} " +
                        "${it.screen.startTime.value.toLocalTime()} " +
                        "좌석: ${it.seats.toDisplayText()}",
            )
        }

        outputView.printMessage(
            "결제 금액: ${"%,d".format(result.paidAmount.amount)}원  " +
                    "(포인트 ${"%,d".format(result.usedPoint)}원 사용)",
        )
        outputView.printMessage("")
        outputView.printMessage("감사합니다.")
    }

    private fun selectedScreeningReservedSeats(screening: Screening): Seats {
        val sameScreen =
            repository.screenings.firstOrNull {
                it.movie == screening.movie && it.startTime == screening.startTime
            } ?: screening

        return sameScreen.reservedSeatsFrom(allSeats)
    }

    private fun updateScreeningReservation(
        screening: Screening,
        selectedSeats: Seats,
    ) {
        repository.updateScreening(
            repository.screenings.map {
                if (it.movie == screening.movie && it.startTime == screening.startTime) {
                    it.reserve(selectedSeats.values)
                } else {
                    it
                }
            },
        )
    }

    private fun <T> retryPrompt(action: () -> T): T {
        while (true) {
            try {
                return action()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}

package domain.payment

import controller.ScreeningMockData
import domain.account.Account
import domain.account.Point
import domain.reservation.Cart
import domain.reservation.ReservedScreen
import domain.reservation.Seat
import domain.reservation.SeatColumn
import domain.reservation.SeatGrade
import domain.reservation.SeatRow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PaymentTest {
    private val discountPolicy = DiscountPolicy()
    private val emptyCart = Cart(emptyList())
    private val containedCart =
        emptyCart.add(
            ReservedScreen(
                screen = ScreeningMockData.screenings().first(),
                seats =
                    listOf(
                        Seat(SeatRow("A"), SeatColumn(2), SeatGrade.S),
                        Seat(SeatRow("B"), SeatColumn(2), SeatGrade.B),
                    ),
            ),
        )
    private val payment =
        Payment(
            cart = emptyCart,
            discountPolicy = discountPolicy,
        )

    @Test
    fun `포인트 사용액이 결제 금액 이하이면 해당 금액만큼 차감된다`() {
        val account = Account()

        val result =
            payment.applyPoint(
                amount = 10_000,
                account = account,
                point = 2_000,
            )

        assertEquals(8_000, result)
    }

    @Test
    fun `포인트 사용액을 0으로 입렫하면 포인트를 차감하지 않는다`() {
        val account = Account()

        val result =
            payment.applyPoint(
                amount = 1_000,
                account = account,
                point = 0,
            )

        assertEquals(1000, result)
    }

    @Test
    fun `카트에 담긴 좌석들의 날짜 및 시간 할인 적용 금액을 계산한다`() {
        val newPayment =
            Payment(
                cart = containedCart,
                discountPolicy = discountPolicy,
            )

        assertEquals(25000, newPayment.discountedTotalAmount())
    }

    @Test
    fun `여러 할인 혜택과 포인트를 적용한 후 최종 금액을 계산한다`() {
        val newPayment =
            Payment(
                cart = containedCart,
                discountPolicy = discountPolicy,
            )

        val result =
            newPayment.pay(
                pointAmount = 1000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        assertEquals(22_800, (result as PayResult.Success).paidAmount)
    }

    @Test
    fun `결제에 실패하면 IllegalArgumentException을 발생시킨다`() {
        val result1 =
            payment.pay(
                pointAmount = 3000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )
        val result2 =
            payment.pay(
                pointAmount = 2000,
                account = Account(Point(0)),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        assertTrue(result1 is PayResult.Failure)
        assertTrue(result2 is PayResult.Failure)
    }
}

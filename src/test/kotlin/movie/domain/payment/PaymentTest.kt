package movie.domain.payment

import movie.controller.ScreeningMockData
import movie.domain.account.Account
import movie.domain.account.Point
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatRow
import movie.domain.reservation.Seats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PaymentTest {
    private val emptyCart = Cart()
    private val pointPolicy = PointUsage
    private val point = Point(2_000)

    @Test
    fun `정책들을 순서대로 적용한 최종 결제 금액을 반환한다`() {
        val payment =
            Payment(
                cart = emptyCart,
                paymentPolicy =
                    listOf(
                        AddAmountPolicy(10_000),
                        SubtractAmountPolicy(1_000),
                        PaymentMethodDiscountPolicyStub,
                    ),
            )

        val result =
            payment.pay(
                pointAmount = 2_000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        assertTrue(result is PayResult.Success)
        result as PayResult.Success
        assertEquals(8_550, result.paidAmount)
    }

    @Test
    fun `정책에서 사용 포인트를 반영하면 성공 결과에 usedPoint가 담긴다`() {
        val payment =
            Payment(
                cart = emptyCart,
                paymentPolicy =
                    listOf(
                        AddAmountPolicy(10_000),
                        UsePointPolicyStub,
                    ),
            )

        val result =
            payment.pay(
                pointAmount = 2_000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CASH,
            )

        assertTrue(result is PayResult.Success)
        result as PayResult.Success
        assertThat(result.paidAmount).isEqualTo(8_000)
        assertThat(result.usedPoint).isEqualTo(2_000)
    }

    @Test
    fun `정책 적용 중 예외가 발생하면 실패 결과를 반환한다`() {
        val payment =
            Payment(
                cart = emptyCart,
                paymentPolicy =
                    listOf(
                        AddAmountPolicy(10_000),
                        FailPolicy("결제 중 오류가 발생했습니다."),
                    ),
            )

        val result =
            payment.pay(
                pointAmount = 1_000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        assertTrue(result is PayResult.Failure)
        result as PayResult.Failure
        assertEquals("결제 중 오류가 발생했습니다.", result.message)
    }

    @Test
    fun `포인트 사용액이 결제 금액 이하이면 해당 금액만큼 차감된다`() {
        val context =
            PaymentContext(
                cart = emptyCart,
                account = Account(point),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
                requestedPoint = 2_000,
                amount = 10_000,
            )

        val result = pointPolicy.apply(context)

        assertThat(result.amount).isEqualTo(8_000)
        assertThat(result.usedPoint).isEqualTo(2_000)
    }

    @Test
    fun `포인트 사용액을 0으로 입력하면 포인트를 차감하지 않는다`() {
        val context =
            PaymentContext(
                cart = emptyCart,
                account = Account(point),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
                requestedPoint = 0,
                amount = 1_000,
            )

        val result = pointPolicy.apply(context)

        assertThat(result.amount).isEqualTo(1_000)
        assertThat(result.usedPoint).isEqualTo(0)
    }

    @Test
    fun `포인트 사용액이 결제 금액을 초과하면 예외가 발생한다`() {
        val context =
            PaymentContext(
                cart = emptyCart,
                account = Account(Point(5_000)),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
                requestedPoint = 3_000,
                amount = 2_000,
            )

        val exception =
            kotlin
                .runCatching {
                    pointPolicy.apply(context)
                }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals("포인트 사용액수는 구매금액을 초과할 수 없습니다.", exception?.message)
    }

    @Test
    fun `보유 포인트보다 많이 사용하려고 하면 예외가 발생한다`() {
        val context =
            PaymentContext(
                cart = emptyCart,
                account = Account(point),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
                requestedPoint = 3_000,
                amount = 10_000,
            )

        val exception =
            kotlin
                .runCatching {
                    pointPolicy.apply(context)
                }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals("보유 포인트가 부족합니다.", exception?.message)
    }

    private val cart =
        Cart().add(
            ReservedScreen(
                screen = ScreeningMockData.screenings().first(),
                seats =
                    Seats.create(
                        listOf(
                            Seat(SeatRow("A"), SeatColumn(2)),
                            Seat(SeatRow("B"), SeatColumn(2)),
                        ),
                    ),
            ),
        )

    @Test
    fun `카트에 담긴 좌석들의 날짜 및 시간 할인 적용 금액을 계산한다`() {
        val context =
            PaymentContext(
                cart = cart,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
                requestedPoint = 0,
                amount = 0,
            )

        val result = ScreeningDiscount().apply(context)

        assertEquals(19_600, result.amount)
    }

    private class AddAmountPolicy(
        private val amountToAdd: Int,
    ) : PaymentPolicy {
        override fun apply(context: PaymentContext): PaymentContext = context.copy(amount = context.amount + amountToAdd)
    }

    private class SubtractAmountPolicy(
        private val amountToSubtract: Int,
    ) : PaymentPolicy {
        override fun apply(context: PaymentContext): PaymentContext = context.copy(amount = context.amount - amountToSubtract)
    }

    private object UsePointPolicyStub : PaymentPolicy {
        override fun apply(context: PaymentContext): PaymentContext =
            context.copy(
                amount = context.amount - context.requestedPoint,
                usedPoint = context.requestedPoint,
            )
    }

    private object PaymentMethodDiscountPolicyStub : PaymentPolicy {
        override fun apply(context: PaymentContext): PaymentContext =
            context.copy(
                amount = context.selectedPaymentMethod.calculateDiscount(context.amount),
            )
    }

    private class FailPolicy(
        private val message: String,
    ) : PaymentPolicy {
        override fun apply(context: PaymentContext): PaymentContext = throw IllegalArgumentException(message)
    }
}

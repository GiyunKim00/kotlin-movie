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
import org.junit.jupiter.api.Test

class PaymentTest {
    private val discountPolicy =
        DiscountPolicy(
            listOf(
                MovieDayDiscountMethod,
                TimeSaleDiscountMethod,
            ),
        )

    private val cart =
        Cart().add(
            ReservedScreen(
                screen = ScreeningMockData.screenings().first(),
                seats =
                    Seats(
                        listOf(
                            Seat(SeatRow("A"), SeatColumn(2)),
                            Seat(SeatRow("B"), SeatColumn(2)),
                        ),
                    ),
            ),
        )

    @Test
    fun `여러 할인 혜택과 포인트를 적용한 후 최종 결제 금액을 반환한다`() {
        // given
        val payment =
            Payment(
                cart = cart,
                discountPolicy = discountPolicy,
            )

        // when
        val result =
            payment.pay(
                pointAmount = 2_000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        // then
        assertThat(result).isInstanceOf(PayResult.Success::class.java)
        result as PayResult.Success
        assertThat(result.paidAmount).isEqualTo(Money(16_720))
        assertThat(result.usedPoint).isEqualTo(2_000)
    }

    @Test
    fun `포인트를 사용하면 성공 결과에 usedPoint가 담긴다`() {
        // given
        val payment =
            Payment(
                cart = cart,
                discountPolicy = discountPolicy,
            )

        // when
        val result =
            payment.pay(
                pointAmount = 2_000,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CASH,
            )

        // then
        assertThat(result).isInstanceOf(PayResult.Success::class.java)
        result as PayResult.Success
        assertThat(result.paidAmount).isEqualTo(Money(17_248))
        assertThat(result.usedPoint).isEqualTo(2_000)
    }

    @Test
    fun `보유 포인트보다 많이 사용하려고 하면 실패 결과를 반환한다`() {
        // given
        val payment =
            Payment(
                cart = cart,
                discountPolicy = discountPolicy,
            )

        // when
        val result =
            payment.pay(
                pointAmount = 2_000,
                account = Account(Point(0)),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        // then
        assertThat(result).isInstanceOf(PayResult.Failure::class.java)
        result as PayResult.Failure
        assertThat(result.message).isEqualTo("보유 포인트가 부족합니다.")
    }

    @Test
    fun `포인트를 사용하지 않으면 결제 수단 할인만 적용된다`() {
        // given
        val payment =
            Payment(
                cart = cart,
                discountPolicy = discountPolicy,
            )

        // when
        val result =
            payment.pay(
                pointAmount = 0,
                account = Account(),
                selectedPaymentMethod = PaymentMethod.CREDIT_CARD,
            )

        // then
        assertThat(result).isInstanceOf(PayResult.Success::class.java)
        result as PayResult.Success
        assertThat(result.paidAmount).isEqualTo(Money(18_620))
        assertThat(result.usedPoint).isEqualTo(0)
    }
}

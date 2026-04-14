package movie.domain.payment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PaymentMethodTest {
    private val totalMoney = Money(10_000)

    @Test
    fun `신용 카드 결제 선택 시 총액 기준 5% 할인된다`() {
        // when
        val creditCardDiscount = PaymentMethod.CREDIT_CARD.calculateDiscount(totalMoney)

        // then
        assertThat(creditCardDiscount).isEqualTo(Money(9_500))
    }

    @Test
    fun `현금 결제 선택 시 총액 기준 2% 할인된다`() {
        // when
        val cashDiscount = PaymentMethod.CASH.calculateDiscount(totalMoney)

        // then
        assertThat(cashDiscount).isEqualTo(Money(9_800))
    }

    @Test
    fun `결제 수단을 숫자로 선택한다`() {
        // given & when
        val paymentMethod1 = PaymentMethod.validate(1)
        val paymentMethod2 = PaymentMethod.validate(2)

        // then
        assertTrue(paymentMethod1 == PaymentMethod.CREDIT_CARD)
        assertTrue(paymentMethod2 == PaymentMethod.CASH)
    }

    @Test
    fun `결제 수단 외 글자가 들어올 시 IllegalArgumentException을 반환한다`() {
        assertThrows<IllegalArgumentException> { PaymentMethod.validate(3) }
        assertThrows<IllegalArgumentException> { PaymentMethod.validate(123) }
    }
}

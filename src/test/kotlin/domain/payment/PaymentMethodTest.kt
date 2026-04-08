package domain.payment

import org.junit.jupiter.api.Test

class PaymentMethodTest {
    @Test
    fun `신용 카드 결제 선택 시 총액 기준 5% 할인된다`() {
        val totalMoney = 10000
        val creditCardDiscount = PaymentMethod.CREDIT_CARD.calculateDiscount(totalMoney)
        assert(creditCardDiscount == 9500)
    }

    @Test
    fun `현금 결제 선택 시 총액 기준 2% 할인된다`() {
        val totalMoney = 10000
        val creditCardDiscount = PaymentMethod.CASH.calculateDiscount(totalMoney)
        assert(creditCardDiscount == 9800)
    }
}
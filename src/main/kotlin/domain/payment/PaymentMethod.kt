package domain.payment

enum class PaymentMethod(
    val discountRate: Double,
) {
    CREDIT_CARD(0.05),
    CASH(0.02),
    ;

    companion object {
        fun validate(number: Int): PaymentMethod =
            when (number) {
                1 -> CREDIT_CARD
                2 -> CASH
                else -> throw IllegalArgumentException("올바른 결제 수단을 선택해 주세요.")
            }
    }

    fun calculateDiscount(price: Int): Int = (price * (1 - discountRate)).toInt()
}

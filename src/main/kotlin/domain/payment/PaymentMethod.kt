package domain.payment

enum class PaymentMethod(val discountRate: Double) {
    CREDIT_CARD(0.05),
    CASH(0.02);

    fun calculateDiscount(price: Int): Int = (price * (1 - discountRate)).toInt()
}

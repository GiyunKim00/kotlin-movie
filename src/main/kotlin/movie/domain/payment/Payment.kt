package movie.domain.payment

import movie.domain.account.Account
import movie.domain.reservation.Cart

class Payment(
    val cart: Cart,
    val discountPolicy: DiscountPolicy,
) {
    fun pay(
        pointAmount: Int = 0,
        account: Account,
        selectedPaymentMethod: PaymentMethod,
    ): PayResult =
        runCatching {
            val discountedTotalAmount = discountedTotalAmount()
            val amountAfterPoint = applyPoint(discountedTotalAmount, account, pointAmount)
            val paidAmount = selectPaymentMethod(amountAfterPoint, selectedPaymentMethod)

            PayResult.Success(
                cart = cart,
                paidAmount = paidAmount,
                usedPoint = pointAmount,
                paymentMethod = selectedPaymentMethod,
            )
        }.getOrElse { exception ->
            PayResult.Failure(
                message = exception.message ?: "결제에 실패했습니다.",
            )
        }

    fun applyPoint(
        amount: Int,
        account: Account,
        point: Int,
    ): Int {
        require(amount >= point) { "포인트 사용액수는 구매금액을 초과할 수 없습니다." }

        account.useMyPoint(point)
        return amount - point
    }

    fun selectPaymentMethod(
        amount: Int,
        paymentMethod: PaymentMethod,
    ): Int = paymentMethod.calculateDiscount(amount)

    fun discountedTotalAmount(): Int =
        cart.items.sumOf { reserved ->
            discountPolicy.discount(reserved.screen.startTime, reserved.price())
        }
}

sealed interface PayResult {
    data class Success(
        val cart: Cart,
        val paidAmount: Int,
        val usedPoint: Int,
        val paymentMethod: PaymentMethod,
    ) : PayResult

    data class Failure(
        val message: String,
    ) : PayResult
}

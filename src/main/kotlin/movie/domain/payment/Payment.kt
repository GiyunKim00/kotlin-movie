package movie.domain.payment

import movie.domain.account.Account
import movie.domain.reservation.Cart
import kotlin.collections.fold

class Payment(
    private val cart: Cart,
    private val discountPolicy: DiscountPolicy,
) {
    fun pay(
        pointAmount: Int = 0,
        account: Account,
        selectedPaymentMethod: PaymentMethod,
    ): PayResult =
        runCatching {
            val discountedAmount = discountedTotalAmount()
            validatePointUsage(discountedAmount, pointAmount, account)

            val amountAfterPoint = discountedAmount - Money(pointAmount)
            val paidAmount = selectedPaymentMethod.calculateDiscount(amountAfterPoint)

            account.useMyPoint(pointAmount)

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

    private fun discountedTotalAmount(): Money =
        cart.reservedScreens.fold(Money(0)) { totalAmount, reserved ->
            totalAmount.plus(
                discountPolicy.discount(
                    reserved.screen.startTime,
                    reserved.price(),
                ),
            )
        }

    private fun validatePointUsage(
        discountedAmount: Money,
        pointAmount: Int,
        account: Account,
    ) {
        require(discountedAmount >= pointAmount) {
            "포인트 사용액수는 구매금액을 초과할 수 없습니다."
        }
        account.validateUsablePoint(pointAmount)
    }
}

sealed interface PayResult {
    data class Success(
        val cart: Cart,
        val paidAmount: Money,
        val usedPoint: Int,
        val paymentMethod: PaymentMethod,
    ) : PayResult

    data class Failure(
        val message: String,
    ) : PayResult
}

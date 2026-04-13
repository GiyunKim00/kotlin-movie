package movie.domain.payment

import movie.domain.account.Account
import movie.domain.reservation.Cart

class Payment(
    val cart: Cart,
    private val paymentPolicy: List<PaymentPolicy>,
) {
    fun pay(
        pointAmount: Int = 0,
        account: Account,
        selectedPaymentMethod: PaymentMethod,
    ): PayResult =
        runCatching {
            val initialContext = PaymentContext(
                cart = cart,
                account = account,
                selectedPaymentMethod = selectedPaymentMethod,
                requestedPoint = pointAmount,
                amount = 0,
            )

            val result = paymentPolicy.fold(initialContext) { context, policy ->
                policy.apply(context)
            }

            PayResult.Success(
                cart = result.cart,
                paidAmount = result.amount,
                usedPoint = result.usedPoint,
                paymentMethod = result.selectedPaymentMethod,
            )
        }.getOrElse { exception ->
            PayResult.Failure(
                message = exception.message ?: "결제에 실패했습니다.",
            )
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

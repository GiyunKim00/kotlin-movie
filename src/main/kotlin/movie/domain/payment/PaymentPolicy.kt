package movie.domain.payment

import movie.domain.account.Account
import movie.domain.reservation.Cart

data class PaymentContext(
    val cart: Cart,
    val account: Account,
    val selectedPaymentMethod: PaymentMethod,
    val requestedPoint: Int,
    val amount: Int,
    val usedPoint: Int = 0,
)

interface PaymentPolicy {
    fun apply(context: PaymentContext): PaymentContext
}

class ScreeningDiscount(
    private val discountPolicy: DiscountPolicy = DiscountPolicy(),
) : PaymentPolicy {
    override fun apply(context: PaymentContext): PaymentContext {
        val discountedAmount =
            context.cart.reservedScreens.sumOf { reserved ->
                discountPolicy.discount(reserved.screen.startTime, reserved.price())
            }

        return context.copy(amount = discountedAmount)
    }
}

object PointUsage : PaymentPolicy {
    override fun apply(context: PaymentContext): PaymentContext {
        val point = context.requestedPoint
        require(context.amount >= point) { "포인트 사용액수는 구매금액을 초과할 수 없습니다." }

        context.account.useMyPoint(point)

        return context.copy(
            amount = context.amount - point,
            usedPoint = point,
        )
    }
}

object PaymentMethodDiscount : PaymentPolicy {
    override fun apply(context: PaymentContext): PaymentContext {
        val discounted = context.selectedPaymentMethod.calculateDiscount(context.amount)
        return context.copy(amount = discounted)
    }
}
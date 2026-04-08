package domain.payment

import domain.account.Account
import domain.screening.ReservedScreening

class Payment(
    val reservedScreening: ReservedScreening,
    val discountPolicy: DiscountPolicy,
) {
    fun pay(
        pointAmount: Int = 0,
        account: Account,
        selectedPaymentMethod: PaymentMethod
    ): PayResult {
        return runCatching {
            val discountedTotalAmount = discountedTotalAmount()
            val amountAfterPoint = applyPoint(discountedTotalAmount, account, pointAmount)
            val paidAmount = selectPaymentMethod(amountAfterPoint, selectedPaymentMethod)

            PayResult.Success(
                reservedScreening = reservedScreening,
                paidAmount = paidAmount,
                usedPoint = pointAmount,
                paymentMethod = selectedPaymentMethod,
            )
        }.getOrElse { exception ->
            PayResult.Failure(
                message = exception.message ?: "결제에 실패했습니다.",
            )
        }
    }

    fun applyPoint(amount: Int, account: Account, point: Int): Int {
        require(amount >= point) { "포인트 사용액수는 구매금액을 초과할 수 없습니다." }

        account.useMyPoint(point)
        return amount - point
    }

    fun selectPaymentMethod(amount: Int, paymentMethod: PaymentMethod): Int =
        paymentMethod.calculateDiscount(amount)

    fun discountedTotalAmount(): Int =
        reservedScreening.items.sumOf { screening ->
            val amount = screening.price()
            discountPolicy.discount(screening.startTime, amount)
        }
}

sealed interface PayResult {
    data class Success(
        val reservedScreening: ReservedScreening,
        val paidAmount: Int,
        val usedPoint: Int,
        val paymentMethod: PaymentMethod,
    ) : PayResult

    data class Failure(
        val message: String,
    ) : PayResult
}
package movie.controller.service

import movie.domain.account.Account
import movie.domain.payment.DiscountPolicy
import movie.domain.payment.MovieDayDiscountMethod
import movie.domain.payment.PayResult
import movie.domain.payment.Payment
import movie.domain.payment.PaymentMethod
import movie.domain.payment.TimeSaleDiscountMethod
import movie.domain.reservation.Cart

class PaymentService(
    private val account: Account,
) {
    fun validatePoint(inputPoint: Int): Int {
        require(account.point.amount >= inputPoint) {
            "보유 포인트가 부족합니다."
        }
        return inputPoint
    }

    fun validatePaymentMethod(input: Int): PaymentMethod = PaymentMethod.validate(input)

    fun pay(
        cart: Cart,
        point: Int,
        paymentMethod: PaymentMethod,
    ): PayResult {
        val discountPolicy =
            DiscountPolicy(
                listOf(
                    MovieDayDiscountMethod,
                    TimeSaleDiscountMethod,
                ),
            )

        val payment =
            Payment(
                cart = cart,
                discountPolicy = discountPolicy,
            )

        return payment.pay(point, account, paymentMethod)
    }
}

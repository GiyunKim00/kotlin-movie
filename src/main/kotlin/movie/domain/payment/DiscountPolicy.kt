package movie.domain.payment

import movie.domain.screening.ScreeningStartTime

class DiscountPolicy(
    private val discountMethods: List<DiscountMethod> =
        listOf(
            MovieDayDiscountMethod,
            TimeSaleDiscountMethod,
        ),
) {
    /**
     * for문과 동일한 연산을 하는 fold 활용.
     * 초기값 amount -> currentAmount = rule.apply(screentime, currentAmount) 값이 적용됨.
     */
    fun discount(
        screeningStartTime: ScreeningStartTime,
        amount: Int,
    ): Int =
        discountMethods.fold(amount) { currentAmount, rule ->
            rule.apply(screeningStartTime, currentAmount)
        }
}

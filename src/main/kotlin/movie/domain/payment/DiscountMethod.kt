package movie.domain.payment

import movie.domain.screening.ScreeningStartTime

interface DiscountMethod {
    fun apply(
        date: ScreeningStartTime,
        money: Money,
    ): Money
}

object MovieDayDiscountMethod : DiscountMethod {
    private const val MOVIE_DAY_DISCOUNT_RATE = 0.9
    private val MOVIE_DAYS = listOf(10, 20, 30)

    override fun apply(
        date: ScreeningStartTime,
        money: Money,
    ): Money {
        val day = date.value.dayOfMonth
        return if (day in MOVIE_DAYS) {
            money.percent(MOVIE_DAY_DISCOUNT_RATE)
        } else {
            money
        }
    }
}

object DiscountByTimeMethod : DiscountMethod {
    private const val TIME_DISCOUNT_AMOUNT = 2_000
    private const val START_HOUR = 11
    private const val END_HOUR = 19

    override fun apply(
        date: ScreeningStartTime,
        money: Money,
    ): Money {
        if (date.value.hour !in START_HOUR..END_HOUR) {
            return money - Money(TIME_DISCOUNT_AMOUNT)
        }
        return money
    }
}

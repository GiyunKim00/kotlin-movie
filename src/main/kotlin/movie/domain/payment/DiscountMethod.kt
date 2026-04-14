package movie.domain.payment

import movie.domain.screening.ScreeningStartTime

interface DiscountMethod {
    fun apply(
        date: ScreeningStartTime,
        money: Money,
    ): Money
}

object MovieDayDiscountMethod : DiscountMethod {
    override fun apply(
        date: ScreeningStartTime,
        money: Money,
    ): Money {
        val day = date.value.dayOfMonth
        if (day == 10 || day == 20 || day == 30) return money.percent(0.9)
        return money
    }
}

object TimeSaleDiscountMethod : DiscountMethod {
    override fun apply(
        date: ScreeningStartTime,
        money: Money,
    ): Money {
        if (date.value.hour !in 11..19) return money - Money(2000)
        return money
    }
}

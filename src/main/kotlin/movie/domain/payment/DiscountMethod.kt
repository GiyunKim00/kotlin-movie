package movie.domain.payment

import movie.domain.screening.ScreeningStartTime

interface DiscountMethod {
    fun apply(
        date: ScreeningStartTime,
        money: Int,
    ): Int
}

object MovieDayDiscountMethod : DiscountMethod {
    override fun apply(
        date: ScreeningStartTime,
        money: Int,
    ): Int {
        val day = date.value.dayOfMonth
        if (day == 10 || day == 20 || day == 30) return (money * 0.9).toInt()
        return money
    }
}

object TimeSaleDiscountMethod : DiscountMethod {
    override fun apply(
        date: ScreeningStartTime,
        money: Int,
    ): Int {
        if (date.value.hour !in 11..19) return money - 2000
        return money
    }
}

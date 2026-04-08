package domain.payment

import domain.screening.ScreeningStartTime

class DiscountPolicy {

    fun discount(date: ScreeningStartTime, money: Int): Int {
        return discountByTimeSale(
            date,
            discountByMovieDay(date.value.dayOfMonth, money)
        )
    }

    fun discountByMovieDay(day: Int, money: Int): Int {
        if (day == 10 || day == 20 || day == 30) {
            return (money * 0.9).toInt()
        }
        return money
    }

    fun discountByTimeSale(time: ScreeningStartTime, money: Int): Int {
        if (time.value.hour !in 11..19) {
            return money - 2000
        }
        return money
    }
}
package movie.domain.reservation

import movie.domain.payment.Money

enum class SeatGrade(
    val money: Money,
) {
    S(money = Money(18_000)),
    A(money = Money(15_000)),
    B(money = Money(12_000)),
    ;

    companion object {
        fun grantGrade(row: SeatRow): SeatGrade =
            when (row.value) {
                "A", "B" -> B
                "C", "D" -> S
                "E" -> A
                else -> throw IllegalArgumentException("유효하지 않은 좌석 행입니다.")
            }
    }
}

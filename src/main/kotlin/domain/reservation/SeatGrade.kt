package domain.reservation

enum class SeatGrade(val money: Int) {
    S(money = 18_000),
    A(money = 15_000),
    B(money = 12_000)
}
package movie.domain.reservation

enum class SeatGrade(
    val money: Int,
) {
    S(money = 18_000),
    A(money = 15_000),
    B(money = 12_000),
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

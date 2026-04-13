package movie.domain.account

class Point(
    val amount: Int,
) {
    init {
        require(amount >= 0) { "포인트는 0원 이상이어야 합니다." }
    }

    fun usePoint(usedAmount: Int): Point = Point(amount - usedAmount)
}

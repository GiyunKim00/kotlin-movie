package domain.account

class Point(val amount: Int) {
    fun usePoint(usedAmount: Int): Point {
        require(amount > 0 || amount >= usedAmount) { "포인트가 부족합니다." }

        return Point(amount - usedAmount)
    }
}
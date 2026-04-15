package movie.domain.account

class Account(
    var point: Point = Point(2000),
) {
    fun useMyPoint(usingPoints: Int) {
        validateUsablePoint(usingPoints)
        point = point.usePoint(usingPoints)
    }

    fun validateUsablePoint(usingPoints: Int) {
        require(usingPoints >= 0) { "올바른 포인트 사용액수가 아닙니다." }
        require(point >= usingPoints) { "보유 포인트가 부족합니다." }
    }
}

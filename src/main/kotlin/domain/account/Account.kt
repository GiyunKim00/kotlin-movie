package domain.account

class Account(var point: Point = Point(2000)) {
    fun useMyPoint(usingPoints: Int) {
        point = point.usePoint(usingPoints)
    }
}
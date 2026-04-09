package domain.reservation

class Cart(
    private var reservedScreen: List<ReservedScreen> = emptyList(),
) {
    val items: List<ReservedScreen> = reservedScreen.toList()

    fun add(screening: ReservedScreen): Cart {
        reservedScreen.forEach {
            require(!it.screen.overlaps(screening.screen)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }
        }
        return Cart(reservedScreen + screening)
    }
}

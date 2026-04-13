package movie.domain.reservation

class Cart(
    val reservedScreens: List<ReservedScreen> = emptyList(),
) {

    fun add(reservedScreen: ReservedScreen): Cart {
        reservedScreens.forEach {
            require(!it.screen.overlaps(reservedScreen.screen)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }
        }
        return Cart(reservedScreens + reservedScreen)
    }
}

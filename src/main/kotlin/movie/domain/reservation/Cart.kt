package movie.domain.reservation

import movie.domain.screening.Screening

class Cart(
    val reservedScreens: List<ReservedScreen> = emptyList(),
) {
    fun add(reservedScreen: ReservedScreen): Cart = Cart(reservedScreens + reservedScreen)

    fun validateOverlap(screening: Screening) {
        reservedScreens.forEach {
            require(!it.screen.overlaps(screening)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }
        }
    }
}

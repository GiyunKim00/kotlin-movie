package movie.domain.reservation

import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CartTest {
    private val cart = Cart()

    private fun createScreening(
        start: LocalDateTime,
        runningMinutes: Int,
    ): Screening {
        val movie =
            Movie(
                MovieTitle("테스트 영화"),
                RunningTime(runningMinutes),
            )
        return Screening(
            movie = movie,
            startTime = ScreeningStartTime(start),
        )
    }

    private fun reserved(screening: Screening): ReservedScreen = ReservedScreen(screening, Seats.create(emptyList()))

    @Test
    fun `상영 시간이 겹치지 않으면 Cart에 정상 추가된다`() {
        // given
        val screening1 =
            createScreening(
                start = LocalDateTime.of(2025, 1, 1, 10, 0),
                runningMinutes = 120,
            )

        val screening2 =
            createScreening(
                start = LocalDateTime.of(2025, 1, 1, 13, 0),
                runningMinutes = 120,
            )

        // when
        val result =
            cart
                .add(reserved(screening1))
                .add(reserved(screening2))

        // then
        assertEquals(2, result.reservedScreens.size)
    }

    @Test
    fun `상영 시간이 겹치면 예외가 발생한다`() {
        // given
        val screening1 =
            createScreening(
                start = LocalDateTime.of(2025, 1, 1, 10, 0),
                runningMinutes = 120,
            )

        val screening2 =
            createScreening(
                start = LocalDateTime.of(2025, 1, 1, 11, 0),
                runningMinutes = 120,
            )

        val newCart = cart.add(reserved(screening1))

        // when & then
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                newCart.add(reserved(screening2))
            }

        assertEquals(
            "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요.",
            exception.message,
        )
    }
}

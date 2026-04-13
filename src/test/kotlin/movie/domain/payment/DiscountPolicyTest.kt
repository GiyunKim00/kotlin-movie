package movie.domain.payment

import movie.domain.screening.ScreeningStartTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DiscountPolicyTest {
    private val discountPolicy = DiscountPolicy()
    private val totalAmount = 10000
    val movieDayDiscountRate = 0.9
    val timeDiscountAmount = 2000

    val expectedMovieDayDiscountAmount = (totalAmount * movieDayDiscountRate).toInt()
    val expectedTimeDiscountAmount = totalAmount - timeDiscountAmount
    @Test
    fun `10일, 20일, 30일은 무비데이 할인이 적용된다`() {
        val movieDays = listOf(10, 20, 30)

        for(i in movieDays) {
            val movieDay = ScreeningStartTime(LocalDateTime.of(2026, 5, i, 13, 0))
            assertThat(expectedMovieDayDiscountAmount).isEqualTo(discountPolicy.discount(movieDay, totalAmount))
        }
    }

    @Test
    fun `10일, 20일, 30일 외의 날은 무비데이 할인이 적용되지 않는다`() {
        val movieDays = listOf(11, 21, 29)

        for(i in movieDays) {
            val movieDay = ScreeningStartTime(LocalDateTime.of(2026, 5, i, 13, 0))
            assertThat(expectedMovieDayDiscountAmount).isNotEqualTo(discountPolicy.discount(movieDay, totalAmount))
        }
    }

    @Test
    fun `11시 이전 또는 20시부터 상영하는 영화는 정해진 금액만큼 할인된다`() { // 20 <= ...(자정)... < 11
        val movieTimes = listOf(9, 10, 20, 21)

        for(i in movieTimes) {
            val movieDay = ScreeningStartTime(LocalDateTime.of(2026, 5, 1, i, 0))
            assertThat(expectedTimeDiscountAmount).isEqualTo(discountPolicy.discount(movieDay, totalAmount))
        }
    }

    @Test
    fun `11시부터 20시 이전의 영화는 할인되지 않는다`() { // 11 <= ...(정오)... < 20
        val movieTimes = listOf(11, 12, 18, 19)

        for(i in movieTimes) {
            val movieDay = ScreeningStartTime(LocalDateTime.of(2026, 5, 1, i, 0))
            assertThat(expectedTimeDiscountAmount).isNotEqualTo(discountPolicy.discount(movieDay, totalAmount))
        }
    }

    @Test
    fun `무비데이 할인과 조조 심야 할인이 동시 적용될 시 무비데이 할인이 먼저 적용된다`() {
        val movieHour = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 9, 0))
        val expectededAmount = totalAmount * movieDayDiscountRate - timeDiscountAmount
        assertEquals(
            expectededAmount.toInt(),
            discountPolicy.discount(movieHour, totalAmount),
        )
    }
}

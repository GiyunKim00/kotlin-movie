package movie.domain.payment

import movie.domain.screening.ScreeningStartTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime

class DiscountPolicyTest {
    private val discountPolicy = DiscountPolicy(
        listOf(
            MovieDayDiscountMethod,
            TimeSaleDiscountMethod,
        ),
    )

    private val totalAmount = Money(10_000)
    val movieDayDiscountRate = 0.9
    val timeDiscountAmount = 2000

    val expectedMovieDayDiscountAmount = Money((totalAmount.amount * movieDayDiscountRate).toInt())
    val expectedTimeDiscountAmount = Money(totalAmount.amount - timeDiscountAmount)

    @ParameterizedTest(name = "{0}일은 무비데이 할인이 적용된다")
    @ValueSource(ints = [10, 20, 30])
    fun `10일 20일 30일은 무비데이 할인이 적용된다`(day: Int) {
        // given
        val movieDay = ScreeningStartTime(LocalDateTime.of(2026, 5, day, 13, 0))

        // when
        val discountedAmount = discountPolicy.discount(movieDay, totalAmount)

        // then
        assertThat(discountedAmount).isEqualTo(expectedMovieDayDiscountAmount)
    }


    @ParameterizedTest(name = "{0}일은 무비데이 할인이 적용되지 않는다")
    @ValueSource(ints = [11, 21, 29])
    fun `10일 20일 30일 외의 날은 무비데이 할인이 적용되지 않는다`(day: Int) {
        // given
        val movieDay = ScreeningStartTime(LocalDateTime.of(2026, 5, day, 13, 0))

        // when
        val discountedAmount = discountPolicy.discount(movieDay, totalAmount)

        // then
        assertThat(discountedAmount).isEqualTo(totalAmount)
    }

    @ParameterizedTest(name = "{0}시는 시간 할인이 적용된다")
    @ValueSource(ints = [9, 10, 20, 21])
    fun `11시 이전 또는 20시부터 상영하는 영화는 정해진 금액만큼 할인된다`(hour: Int) { // 20 <= ... (자정) ... < 11
        // given
        val movieTime = ScreeningStartTime(LocalDateTime.of(2026, 5, 1, hour, 0))

        // when
        val discountedAmount = discountPolicy.discount(movieTime, totalAmount)

        // then
        assertThat(discountedAmount).isEqualTo(expectedTimeDiscountAmount)
    }

    @ParameterizedTest(name = "{0}시는 시간 할인이 적용되지 않는다")
    @ValueSource(ints = [11, 12, 18, 19])
    fun `11시부터 20시 이전의 영화는 할인되지 않는다`(hour: Int) { // 11 <= ...(정오)... < 20
        // given
        val movieTime = ScreeningStartTime(LocalDateTime.of(2026, 5, 1, hour, 0))

        // when
        val discountedAmount = discountPolicy.discount(movieTime, totalAmount)

        // then
        assertThat(discountedAmount).isEqualTo(totalAmount)
    }

    @Test
    fun `무비데이 할인과 조조 심야 할인이 동시 적용될 시 무비데이 할인이 먼저 적용된다`() {
        // given
        val movieHour = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 9, 0))
        val expectedAmount = Money((totalAmount.amount * movieDayDiscountRate).toInt() - timeDiscountAmount)

        // when
        val discountedAmount = discountPolicy.discount(movieHour, totalAmount)

        // then
        assertThat(discountedAmount).isEqualTo(expectedAmount)
    }
}

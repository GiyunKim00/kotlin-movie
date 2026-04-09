package domain.payment

import domain.screening.ScreeningStartTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DiscountPolicyTest {
    @Test
    fun `10일, 20일, 30일은 10% 무비데이 할인이 적용된다`() {
        val discountPolicy = DiscountPolicy()
        val totalAmount = 10000
        val expectAmount = (10000 * 0.9).toInt()

        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(10, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(20, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(30, totalAmount),
        )
    }

    @Test
    fun `10일, 20일, 30일 외의 날은 10% 무비데이 할인이 적용되지 않는다`() {
        val discountPolicy = DiscountPolicy()
        val totalAmount = 10000
        val expectAmount = 10000

        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(9, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(11, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(29, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(1, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(15, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByMovieDay(19, totalAmount),
        )
    }

    @Test
    fun `11시 이전 또는 20시 이후의 영화는 2000원 할인된다`() {
        val movieHour = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 10, 0))
        val movieHour2 = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 20, 0))
        val movieHour3 = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 10, 59))

        val discountPolicy = DiscountPolicy()
        val totalAmount = 10000
        val expectAmount = 8000

        assertEquals(
            expectAmount,
            discountPolicy.discountByTimeSale(movieHour, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByTimeSale(movieHour2, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByTimeSale(movieHour3, totalAmount),
        )
    }

    @Test
    fun `11시에서 20시 사이의 영화는 할인되지 않는다`() {
        val movieHour1 = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 11, 0))
        val movieHour2 = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 19, 59))

        val discountPolicy = DiscountPolicy()
        val totalAmount = 10000
        val expectAmount = 10000

        assertEquals(
            expectAmount,
            discountPolicy.discountByTimeSale(movieHour1, totalAmount),
        )
        assertEquals(
            expectAmount,
            discountPolicy.discountByTimeSale(movieHour2, totalAmount),
        )
    }

    @Test
    fun `무비데이 할인과 조조 심야 할인이 동시 적용될 시 무비데이 할인이 먼저 적용된다`() {
        val movieHour = ScreeningStartTime(LocalDateTime.of(2026, 5, 10, 9, 0))
        val discountPolicy = DiscountPolicy()
        val totalAmount = 10000
        val expectAmount = 7000
        assertEquals(
            expectAmount,
            discountPolicy.discount(movieHour, totalAmount),
        )
    }
}

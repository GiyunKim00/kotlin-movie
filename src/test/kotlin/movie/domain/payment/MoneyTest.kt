package movie.domain.payment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class MoneyTest {
    @Test
    fun `금액은 0 이상이어야 한다`() {
        assertThrows<IllegalArgumentException> {
            Money(-1)
        }
    }

    @Test
    fun `금액 간 더할 수 있다`() {
        // given
        val result = Money(10_000) + Money(2_000)

        // when & then
        assertThat(result).isEqualTo(Money(12_000))
    }

    @Test
    fun `금액 간 뺄 수 있다`() {
        // given
        val result = Money(10_000) - Money(2_000)

        // when & then
        assertThat(result).isEqualTo(Money(8_000))
    }

    @Test
    fun `금액을 Int로 빼면 차액을 반환한다`() {
        // given
        val result = Money(10_000) - 2_000

        // when & then
        assertThat(result).isEqualTo(Money(8_000))
    }

    @Test
    fun `뺄셈 결과가 음수면 0원을 반환한다`() {
        // given
        val result = Money(1_000) - Money(2_000)

        // when & then
        assertThat(result).isEqualTo(Money(0))
    }

    @Test
    fun `정수 비교가 가능하다`() {
        assertThat(Money(10_000) >= 5_000).isTrue()
        assertThat(Money(10_000) >= 10_000).isTrue()
        assertThat(Money(10_000) >= 20_000).isFalse()
    }

    @Test
    fun `비율 계산 시 소수점 이하는 절사한다`() {
        val result = Money(10_001).percent(0.9)

        assertThat(result).isEqualTo(Money(9_000))
    }
}

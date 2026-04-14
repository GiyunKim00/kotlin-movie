package movie.domain.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AccountTest {
    @Test
    fun `초기 포인트는 제공된 값으로 설정된다`() {
        val givenPointAmount = 5_000
        val account = Account(Point(givenPointAmount))

        assertThat(account.point.amount).isEqualTo(givenPointAmount)
    }

    @Test
    fun `포인트를 사용하면 해당 금액만큼 차감된다`() {
        // given
        val account = Account(Point(2000))

        // when
        account.useMyPoint(1000)

        // then
        assertThat(account.point.amount).isEqualTo(1000)
    }

    @Test
    fun `보유 포인트보다 많이 사용하면 예외가 발생한다`() {
        // given
        val account = Account(Point(1000))

        // when & then
        assertThrows<IllegalArgumentException> {
            account.useMyPoint(2000)
        }
    }

    @Test
    fun `음수 포인트를 사용하려고 하면 예외가 발생한다`() {
        // given
        val account = Account(Point(1000))

        // when & then
        assertThrows<IllegalArgumentException> {
            account.useMyPoint(-1000)
        }
    }
}

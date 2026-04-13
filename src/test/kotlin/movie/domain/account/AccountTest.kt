package movie.domain.account

import movie.domain.account.Account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountTest {
    @Test
    fun `초기 포인트는 제공된 값으로 설정된다`() {
        val givenPointAmount = 5_000
        val account = Account(Point(givenPointAmount))

        assertEquals(givenPointAmount, account.point.amount)
    }
}

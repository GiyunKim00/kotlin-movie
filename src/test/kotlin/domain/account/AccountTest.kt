package domain.account

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountTest {

    @Test
    fun `초기 포인트 2000원이 주어진다`() {
        val account = Account()

        assertEquals(2000, account.point.amount,)
    }
}
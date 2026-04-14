package movie.domain.account

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointTest {
    private val point = Point(2000)

    @Test
    fun `포인트가 사용한 만큼 차감된다`() {
        // given
        val useAmount: Int = 1000

        // when
        val usedPoint = point.usePoint(useAmount)

        // then
        assertEquals(useAmount, usedPoint.amount)
    }

    @Test
    fun `보유 포인트보다 사용 포인트 액수가 많을 시 예외가 발생한다`() {
        // given
        val useAmount: Int = 3000

        // when & then
        assertThrows<IllegalArgumentException> {
            point.usePoint(useAmount)
        }
    }
}

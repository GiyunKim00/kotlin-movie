package movie.domain.reservation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class SeatsPolicyTest {
    private val policy = SeatsPolicy()

    @Test
    fun `올바른 행, 열 번호로 좌석을 생성할 수 있다`() {
        // given
        val seat = policy.createSeat(0, 0)

        // when & then
        assertThat(seat.row.value).isEqualTo("A")
        assertThat(seat.column.value).isEqualTo(1)
    }

    @Test
    fun `행 인덱스가 제한 범위를 벗어나면 예외가 발생한다`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                policy.createSeat(5, 0)
            }

        assertThat(exception.message).isEqualTo("유효하지 않은 행 인덱스입니다.")
    }

    @Test
    fun `열 인덱스가 제한 범위를 벗어나면 예외가 발생한다`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                policy.createSeat(0, 4)
            }

        assertThat(exception.message).isEqualTo("유효하지 않은 열 인덱스입니다.")
    }

    @Test
    fun `행, 열 제한 범위를 반환한다`() {
        assertThat(policy.rowRange()).isEqualTo(0 until 5)
        assertThat(policy.columnRange()).isEqualTo(0 until 4)
    }
}

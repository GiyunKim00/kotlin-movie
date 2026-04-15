package movie.domain.screening

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class MovieTest {
    @Test
    fun `영화 제목이 같으면 true를 반환한다`() {
        val movie = Movie(
            title = MovieTitle("어벤져스"),
            runningTime = RunningTime(120),
        )

        assertThat(movie.isSameTitle("어벤져스")).isTrue()
    }

    @Test
    fun `영화 제목이 다르면 false를 반환한다`() {
        val movie = Movie(
            title = MovieTitle("어벤져스"),
            runningTime = RunningTime(120),
        )

        assertThat(movie.isSameTitle("아이언맨")).isFalse()
    }

    @Test
    fun `영화 제목은 공백일 수 없다`() {
        assertThrows<IllegalArgumentException> {
            MovieTitle("   ")
        }
    }

    @Test
    fun `상영 시간은 0보다 커야 한다`() {
        assertThrows<IllegalArgumentException> {
            RunningTime(0)
        }

        assertThrows<IllegalArgumentException> {
            RunningTime(-10)
        }
    }
}
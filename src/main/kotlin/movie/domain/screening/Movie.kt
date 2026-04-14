package movie.domain.screening

class Movie(
    val title: MovieTitle,
    val runningTime: RunningTime,
)

@JvmInline
value class MovieTitle(
    val value: String,
) {
    init {
        require(value.trim().isNotBlank()) { "영화 제목은 공백일 수 없습니다." }
    }
}

@JvmInline
value class RunningTime(
    val value: Int,
) {
    init {
        require(value > 0) { "상영 시간은 0 이하일 수 없습니다." }
    }
}

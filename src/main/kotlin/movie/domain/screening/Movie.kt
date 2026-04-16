package movie.domain.screening

data class Movie(
    val id: Long? = null,
    val title: MovieTitle,
    val runningTime: RunningTime,
) {
    fun isSameTitle(other: String): Boolean = title == MovieTitle(other)
}

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

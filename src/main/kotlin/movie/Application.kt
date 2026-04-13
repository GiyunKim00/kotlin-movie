package movie

import movie.domain.screening.RunningTime
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

data class Greeting(
    val id: Long,
    val name: String,
) {
    fun sayHello(): String = "Hello, $name!"
}

data class Screening(
    val id: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)

data class Movie(
    val id: Long,
    val title: String,
    val runningTimeMinutes: RunningTime,
    val screenings: List<Screening>,
)

data class Movies(
    val movies: List<Movie>,
)

val movieList: List<Movie> =
    listOf(
        Movie(
            id = 1,
            title = "인터스텔라",
            runningTimeMinutes = RunningTime(169),
            screenings =
                listOf(
                    Screening(
                        id = 101,
                        startAt = LocalDateTime.of(2025, 9, 20, 13, 30, 0),
                        endAt = LocalDateTime.of(2025, 9, 20, 16, 19, 0),
                    ),
                    Screening(
                        id = 102,
                        startAt = LocalDateTime.of(2025, 9, 20, 18, 0, 0),
                        endAt = LocalDateTime.of(2025, 9, 20, 20, 40, 0),
                    ),
                ),
        ),
        Movie(
            id = 2,
            title = "오펜하이머",
            runningTimeMinutes = RunningTime(180),
            screenings =
                listOf(
                    Screening(
                        id = 101,
                        startAt = LocalDateTime.of(2025, 9, 20, 10, 0, 0),
                        endAt = LocalDateTime.of(2025, 9, 20, 13, 0, 0),
                    ),
                ),
        ),
    )

val movies: Movies = Movies(movies = movieList)

@RestController
class GreetingController {
    @GetMapping("/greeting")
    fun hello(
        @RequestParam(required = false) name: String = "kotlin",
    ): Greeting = Greeting(1, name)

    @GetMapping("/api/movies")
    fun getMovies(): Movies = movies
}

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

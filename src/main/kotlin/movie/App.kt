package movie

import movie.controller.CinemaController
import movie.infrastructure.database.ConnectionProvider
import movie.infrastructure.database.DataInitializer
import movie.infrastructure.database.DatabaseInitializer
import movie.repository.JdbcScreeningRepository
import movie.repository.ScreeningRepository
import movie.view.InputView
import movie.view.OutputView

fun main() {
    val repository = createScreeningRepository()

    val controller =
        CinemaController(
            repository = repository,
            inputView = InputView(),
            outputView = OutputView(),
        )

    controller.run()
}

private fun createScreeningRepository(): ScreeningRepository {
    val connectionProvider = ConnectionProvider("jdbc:h2:~/movie-db")

    DatabaseInitializer(connectionProvider).initialize()
    DataInitializer(connectionProvider).initialize()

    return JdbcScreeningRepository(connectionProvider)
}

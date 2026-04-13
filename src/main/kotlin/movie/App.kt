package movie

import movie.controller.CinemaController
import movie.controller.ScreeningMockData
import movie.repository.CinemaRepository
import movie.view.InputView
import movie.view.OutputView

fun main() {
    val repository =
        CinemaRepository(
            screenings = ScreeningMockData.screenings(),
        )
    val controller =
        CinemaController(
            repository = repository,
            inputView = InputView(),
            outputView = OutputView(),
        )

    controller.run()
}

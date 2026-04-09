import controller.CinemaController
import controller.ScreeningMockData
import repository.CinemaRepository
import view.InputView
import view.OutputView

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

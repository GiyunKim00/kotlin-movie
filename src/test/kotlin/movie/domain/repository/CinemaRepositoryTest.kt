package movie.domain.repository

import movie.controller.ScreeningMockData
import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatRow
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.repository.CinemaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CinemaRepositoryTest {
    val cinemaRepository = CinemaRepository(ScreeningMockData.screenings())

    @Test
    fun `제목과 날짜가 주어지면 해당하는 상영을 찾는다`() {
        //given & when
        val resultScreening =
            cinemaRepository.findByMovieTitleAndDate(
                "어벤져스",
                LocalDate.of(2026, 4, 10),
            )

        //then
        assertEquals(
            ScreeningMockData
                .screenings()
                .first()
                .movie.title.value,
            resultScreening
                .first()
                .movie.title.value,
        )
        assertEquals(
            ScreeningMockData
                .screenings()
                .first()
                .startTime.value,
            resultScreening.first().startTime.value,
        )
    }

    @Test
    fun `해당하는 상영이 없다면 빈 리스트가 반환된다`() {
        //given & when
        val resultScreening =
            cinemaRepository.findByMovieTitleAndDate("어벤져스", LocalDate.of(2026, 4, 15))

        //then
        assertEquals(emptyList<Screening>(), resultScreening)
    }

    @Test
    fun `상영 좌석을 예매하면 저장소에 반영되어야 한다`() {
        //given
        val screening = ScreeningMockData.screenings().first()
        val selectedSeats = Seats(listOf(Seat(SeatRow("A"), SeatColumn(1))))
        val repository = CinemaRepository(listOf(screening))

        //when
        repository.reserveSeats(screening, selectedSeats)
        val updatedScreening = repository.findSameScreening(screening)

        //then
        assertThat(updatedScreening?.isReserved(Seat(SeatRow("A"), SeatColumn(1)))).isTrue()
    }
}

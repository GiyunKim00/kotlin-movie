package domain.reservation

import domain.screening.Movie
import domain.screening.Screening
import domain.screening.ScreeningStartTime
import kotlin.collections.find

class Reservation {
    // Reservation은 싱글톤으로 만들어야 하나?
    // Reservation은 전체의 reservation을 다룸. 즉, 해당 Reservation에서 여러번 입력도 받음.

    fun reserveScreening(
        movie: Movie,
        reserveTime: ScreeningStartTime,
        selectedSeats: Seats,
        screeningSchedule: List<Screening>,
    ) {
        val screening = findScreening(movie, reserveTime, screeningSchedule)
        if(!isSeatsAvailable(screening, selectedSeats)) throw IllegalArgumentException("선택된 자리입니다. 다른 자리를 선택해 주세요.")

        // ReservationTicket 반환
    }

    fun findScreening(
        movie: Movie,
        reserveTime: ScreeningStartTime,
        screeningSchedule: List<Screening>,
    ) = screeningSchedule.find { it.startTime == reserveTime && it.isMovie(movie) }
        ?: throw IllegalArgumentException("상영 정보를 찾을 수 없습니다")

    fun isSeatsAvailable(screening: Screening, selectedSeats: Seats): Boolean {
        selectedSeats.allSeats().forEach { seat ->
            if(screening.isReserved(seat)) return false
        }
        return true
    }
}
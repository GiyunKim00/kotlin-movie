package movie.api.controller

import movie.api.dto.ReservationRequest
import movie.api.dto.ReservationResponse
import movie.service.ReservationApiService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
class ReservationApiController(
    private val reservationApiService: ReservationApiService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(
        @RequestBody request: ReservationRequest,
    ): ReservationResponse = reservationApiService.createReservation(request)
}

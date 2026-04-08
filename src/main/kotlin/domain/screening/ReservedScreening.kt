package domain.screening

class ReservedScreening(private val screenings: List<Screening> = emptyList<Screening>()) {
    val items = screenings
}
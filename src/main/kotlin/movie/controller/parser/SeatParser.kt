package movie.controller.parser

object SeatParser {
    fun parse(raw: String): List<String> =
        raw
            .split(",")
            .map { it.trim().uppercase() }
            .filter { it.isNotBlank() }
}

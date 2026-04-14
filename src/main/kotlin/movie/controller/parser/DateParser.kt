package movie.controller.parser

import java.time.LocalDate

object DateParser {
    fun parse(raw: String): LocalDate =
        try {
            LocalDate.parse(raw)
        } catch (e: Exception) {
            throw IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)")
        }
}
package movie.infrastructure.database

import movie.controller.service.MovieScreenings
import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatRow
import movie.domain.reservation.Seats
import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import movie.infrastructure.sql.ScreeningSQL.FIND_BY_MOVIE_TITLE_AND_DATE
import movie.infrastructure.sql.ScreeningSQL.FIND_BY_SCREENING_ID
import movie.infrastructure.sql.ScreeningSQL.FIND_RESERVED_SEATS_BY_SCREENING_ID
import movie.infrastructure.sql.ScreeningSQL.FIND_SCREENINGS_WITH_MOVIES
import movie.infrastructure.sql.ScreeningSQL.INSERT_RESERVED_SEAT
import movie.repository.ScreeningRepository
import java.sql.Connection
import java.sql.Date
import java.time.LocalDate

class JdbcScreeningRepository(
    private val connectionProvider: ConnectionProvider,
) : ScreeningRepository {
    override fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening> {
        val screenings = mutableListOf<Screening>()

        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(FIND_BY_MOVIE_TITLE_AND_DATE).use { statement ->
                statement.setString(1, title)
                statement.setDate(2, Date.valueOf(date))

                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val screeningId = resultSet.getLong("screening_id")
                    val movieId = resultSet.getLong("movie_id")
                    val movieTitle = resultSet.getString("title")
                    val runningTime = resultSet.getInt("running_time")
                    val startTime = resultSet.getTimestamp("start_time").toLocalDateTime()

                    val movie =
                        Movie(
                            id = movieId,
                            title = MovieTitle(movieTitle),
                            runningTime = RunningTime(runningTime),
                        )

                    val reservedSeats = loadReservedSeats(connection, screeningId)

                    screenings +=
                        Screening(
                            id = screeningId,
                            movie = movie,
                            startTime = ScreeningStartTime(startTime),
                            reservedSeats = reservedSeats,
                        )
                }
            }
        }

        return screenings
    }

    override fun reserveSeats(
        screening: Screening,
        selectedSeats: Seats,
    ) {
        val existingScreening =
            findSameScreening(screening)
                ?: throw IllegalArgumentException("존재하지 않는 상영입니다.")

        require(!existingScreening.hasReservedSeat(selectedSeats)) {
            "이미 예약된 좌석은 다시 선택할 수 없습니다."
        }

        val screeningId = requireNotNull(existingScreening.id) { "상영 id가 존재하지 않습니다." }

        connectionProvider.getConnection().use { connection ->
            connection.autoCommit = false

            try {
                connection.prepareStatement(INSERT_RESERVED_SEAT).use { statement ->
                    selectedSeats.values.forEach { seat ->
                        statement.setLong(1, screeningId)
                        statement.setString(2, seat.row.value)
                        statement.setInt(3, seat.column.value)
                        statement.addBatch()
                    }
                    statement.executeBatch()
                }

                connection.commit()
            } catch (e: Exception) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }

    override fun findSameScreening(screening: Screening): Screening? {
        val screeningId = screening.id ?: return null

        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(FIND_BY_SCREENING_ID).use { statement ->
                statement.setLong(1, screeningId)
                val resultSet = statement.executeQuery()

                if (!resultSet.next()) {
                    return null
                }

                val movie =
                    Movie(
                        id = resultSet.getLong("movie_id"),
                        title = MovieTitle(resultSet.getString("title")),
                        runningTime = RunningTime(resultSet.getInt("running_time")),
                    )

                val reservedSeats = loadReservedSeats(connection, screeningId)

                return Screening(
                    id = resultSet.getLong("screening_id"),
                    movie = movie,
                    startTime =
                        ScreeningStartTime(
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                        ),
                    reservedSeats = reservedSeats,
                )
            }
        }
    }

    private fun loadReservedSeats(
        connection: Connection,
        screeningId: Long,
    ): Seats {
        val seats = mutableListOf<Seat>()

        connection.prepareStatement(FIND_RESERVED_SEATS_BY_SCREENING_ID).use { statement ->
            statement.setLong(1, screeningId)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                seats +=
                    Seat(
                        row = SeatRow(resultSet.getString("seat_row")),
                        column = SeatColumn(resultSet.getInt("seat_column")),
                    )
            }
        }

        return Seats(seats)
    }

    override fun findAllMoviesWithScreenings(): List<MovieScreenings> {
        val movieScreenings = mutableListOf<Pair<Movie, Screening>>()

        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(FIND_SCREENINGS_WITH_MOVIES).use { statement ->
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val movie =
                        Movie(
                            id = resultSet.getLong("movie_id"),
                            title = MovieTitle(resultSet.getString("title")),
                            runningTime = RunningTime(resultSet.getInt("running_time")),
                        )

                    val screeningId = resultSet.getLong("screening_id")
                    val screening =
                        Screening(
                            id = screeningId,
                            movie = movie,
                            startTime =
                                ScreeningStartTime(
                                    resultSet.getTimestamp("start_time").toLocalDateTime(),
                                ),
                            reservedSeats = loadReservedSeats(connection, screeningId),
                        )

                    movieScreenings += movie to screening
                }
            }
        }

        return movieScreenings
            .groupBy({ it.first }, { it.second })
            .map { (movie, screenings) ->
                MovieScreenings(
                    movie = movie,
                    screenings = screenings,
                )
            }
    }

    override fun findScreeningById(screeningId: Long): Screening? {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(FIND_BY_SCREENING_ID).use { statement ->
                statement.setLong(1, screeningId)
                val resultSet = statement.executeQuery()

                if (!resultSet.next()) {
                    return null
                }

                return Screening(
                    id = resultSet.getLong("screening_id"),
                    movie =
                        Movie(
                            id = resultSet.getLong("movie_id"),
                            title = MovieTitle(resultSet.getString("title")),
                            runningTime = RunningTime(resultSet.getInt("running_time")),
                        ),
                    startTime =
                        ScreeningStartTime(
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                        ),
                    reservedSeats = loadReservedSeats(connection, screeningId),
                )
            }
        }
    }
}

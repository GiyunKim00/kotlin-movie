package movie.infrastructure.database

import movie.infrastructure.sql.ScreeningSQL.HAS_MOVIE
import movie.infrastructure.sql.ScreeningSQL.INSERT_MOVIE
import movie.infrastructure.sql.ScreeningSQL.INSERT_SCREENINGS
import java.sql.Connection
import java.sql.Statement
import java.sql.Timestamp

class DataInitializer(
    private val connectionProvider: ConnectionProvider,
) {
    fun initialize() {
        connectionProvider.getConnection().use { connection ->
            if (hasMovies(connection)) return

            val movie1 = insertMovie(connection, "어벤져스", 120)
            val movie2 = insertMovie(connection, "인터스텔라", 169)
            val movie3 = insertMovie(connection, "오펜하이머", 180)

            insertScreening(connection, movie1, "2026-04-10 10:00:00")
            insertScreening(connection, movie1, "2026-04-10 13:00:00")
            insertScreening(connection, movie2, "2026-04-10 15:00:00")
            insertScreening(connection, movie2, "2026-04-10 21:00:00")
            insertScreening(connection, movie3, "2026-04-10 18:00:00")
            insertScreening(connection, movie3, "2026-04-10 23:00:00")
        }
    }

    private fun hasMovies(connection: Connection): Boolean {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(HAS_MOVIE)
            resultSet.next()
            return resultSet.getInt(1) > 0
        }
    }

    private fun insertMovie(
        connection: Connection,
        title: String,
        runningTime: Int,
    ): Long {
        connection.prepareStatement(
            INSERT_MOVIE,
            Statement.RETURN_GENERATED_KEYS,
        ).use { statement ->
            statement.setString(1, title)
            statement.setInt(2, runningTime)
            statement.executeUpdate()

            statement.generatedKeys.use { generatedKeys ->
                require(generatedKeys.next()) { "movie id 생성에 실패했습니다." }
                return generatedKeys.getLong(1)
            }
        }
    }

    private fun insertScreening(
        connection: Connection,
        movieId: Long,
        startTime: String,
    ) {
        connection.prepareStatement(INSERT_SCREENINGS).use { statement ->
            statement.setLong(1, movieId)
            statement.setTimestamp(2, Timestamp.valueOf(startTime))
            statement.executeUpdate()
        }
    }
}
package movie.infrastructure.database

import movie.infrastructure.sql.InitializerSQL.CREATE_MOVIES_TABLE
import movie.infrastructure.sql.InitializerSQL.CREATE_RESERVED_SEATS_TABLE
import movie.infrastructure.sql.InitializerSQL.CREATE_SCREENINGS_TABLE

class DatabaseInitializer(
    private val connectionProvider: ConnectionProvider,
) {
    fun initialize() {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_MOVIES_TABLE)
                statement.executeUpdate(CREATE_SCREENINGS_TABLE)
                statement.executeUpdate(CREATE_RESERVED_SEATS_TABLE)
            }
        }
    }
}

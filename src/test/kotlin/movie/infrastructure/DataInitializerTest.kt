package movie.infrastructure

import movie.infrastructure.database.ConnectionProvider
import movie.infrastructure.database.DataInitializer
import movie.infrastructure.database.DatabaseInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class DataInitializerTest {
    private lateinit var connectionProvider: ConnectionProvider
    private lateinit var databaseInitializer: DatabaseInitializer
    private lateinit var dataInitializer: DataInitializer

    @BeforeEach
    fun setUp() {
        connectionProvider = ConnectionProvider("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        databaseInitializer = DatabaseInitializer(connectionProvider)
        dataInitializer = DataInitializer(connectionProvider)

        databaseInitializer.initialize()
        clearTables()
    }

    @Test
    fun `초기 영화와 상영 데이터를 저장한다`() {
        dataInitializer.initialize()

        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                val movieCountResult = statement.executeQuery("select count(*) from movies")
                movieCountResult.next()
                val movieCount = movieCountResult.getInt(1)

                val screeningCountResult = statement.executeQuery("select count(*) from screenings")
                screeningCountResult.next()
                val screeningCount = screeningCountResult.getInt(1)

                assertThat(movieCount).isEqualTo(3)
                assertThat(screeningCount).isEqualTo(6)
            }
        }
    }

    @Test
    fun `이미 영화 데이터가 있으면 중복으로 저장하지 않는다`() {
        dataInitializer.initialize()
        dataInitializer.initialize()

        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                val movieCountResult = statement.executeQuery("select count(*) from movies")
                movieCountResult.next()
                val movieCount = movieCountResult.getInt(1)

                val screeningCountResult = statement.executeQuery("select count(*) from screenings")
                screeningCountResult.next()
                val screeningCount = screeningCountResult.getInt(1)

                assertThat(movieCount).isEqualTo(3)
                assertThat(screeningCount).isEqualTo(6)
            }
        }
    }

    private fun clearTables() {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate("delete from reserved_seats")
                statement.executeUpdate("delete from screenings")
                statement.executeUpdate("delete from movies")
            }
        }
    }
}
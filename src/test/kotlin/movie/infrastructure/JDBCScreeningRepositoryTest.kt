package movie.infrastructure

import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatRow
import movie.infrastructure.database.ConnectionProvider
import movie.infrastructure.database.DataInitializer
import movie.infrastructure.database.DatabaseInitializer
import movie.infrastructure.database.JdbcScreeningRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import kotlin.test.Test

class JdbcScreeningRepositoryTest {
    private lateinit var connectionProvider: ConnectionProvider
    private lateinit var databaseInitializer: DatabaseInitializer
    private lateinit var dataInitializer: DataInitializer
    private lateinit var repository: JdbcScreeningRepository

    @BeforeEach
    fun setUp() {
        connectionProvider = ConnectionProvider("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        databaseInitializer = DatabaseInitializer(connectionProvider)
        dataInitializer = DataInitializer(connectionProvider)
        repository = JdbcScreeningRepository(connectionProvider)

        databaseInitializer.initialize()
        clearTables()
        dataInitializer.initialize()
    }

    @Test
    fun `영화 제목과 날짜로 상영 목록을 조회한다`() {
        val result = repository.findByMovieTitleAndDate(
            title = "어벤져스",
            date = LocalDate.of(2026, 4, 10),
        )

        assertThat(result).hasSize(2)
        assertThat(result[0].movie.title.value).isEqualTo("어벤져스")
        assertThat(result[0].startTime.value.toLocalDate()).isEqualTo(LocalDate.of(2026, 4, 10))
        assertThat(result[0].id).isNotNull()
    }

    @Test
    fun `해당 제목과 날짜의 상영이 없으면 빈 리스트를 반환한다`() {
        val result = repository.findByMovieTitleAndDate(
            title = "없는 영화",
            date = LocalDate.of(2026, 4, 10),
        )

        assertThat(result).isEmpty()
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

    @Test
    fun `조회한 상영에는 이미 예약된 좌석 정보가 포함된다`() {
        val screeningId = findFirstAvengersScreeningId()

        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(
                """
            insert into reserved_seats(screening_id, seat_row, seat_column)
            values (?, ?, ?)
            """.trimIndent(),
            ).use { statement ->
                statement.setLong(1, screeningId)
                statement.setString(2, "A")
                statement.setInt(3, 1)
                statement.executeUpdate()
            }
        }

        val result = repository.findByMovieTitleAndDate(
            title = "어벤져스",
            date = LocalDate.of(2026, 4, 10),
        )

        val firstScreening = result.first()
        assertThat(firstScreening.isReserved(Seat(SeatRow("A"), SeatColumn(1)))).isTrue()
    }

    private fun findFirstAvengersScreeningId(): Long {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(
                """
            select s.id
            from screenings s
            join movies m on s.movie_id = m.id
            where m.title = ?
            order by s.start_time
            """.trimIndent(),
            ).use { statement ->
                statement.setString(1, "어벤져스")
                val resultSet = statement.executeQuery()
                resultSet.next()
                return resultSet.getLong(1)
            }
        }
    }
}
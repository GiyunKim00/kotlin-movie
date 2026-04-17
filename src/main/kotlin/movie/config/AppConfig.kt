package movie.config

import movie.infrastructure.database.ConnectionProvider
import movie.infrastructure.database.DataInitializer
import movie.infrastructure.database.DatabaseInitializer
import movie.infrastructure.database.JdbcScreeningRepository
import movie.repository.ScreeningRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfig(
    @Value("\${app.db.url}")
    private val dbUrl: String,
) {
    @Bean
    fun connectionProvider(): ConnectionProvider = ConnectionProvider(dbUrl)

    @Bean
    fun databaseInitializer(connectionProvider: ConnectionProvider): DatabaseInitializer {
        val initializer = DatabaseInitializer(connectionProvider)
        initializer.initialize()
        return initializer
    }

    @Bean
    fun dataInitializer(
        connectionProvider: ConnectionProvider,
        databaseInitializer: DatabaseInitializer,
    ): DataInitializer {
        val initializer = DataInitializer(connectionProvider)
        initializer.initialize()
        return initializer
    }

    @Bean
    fun screeningRepository(
        connectionProvider: ConnectionProvider,
        dataInitializer: DataInitializer,
    ): ScreeningRepository = JdbcScreeningRepository(connectionProvider)
}

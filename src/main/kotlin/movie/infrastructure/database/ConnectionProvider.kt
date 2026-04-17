package movie.infrastructure.database

import java.sql.Connection
import java.sql.DriverManager

class ConnectionProvider(
    private val url: String,
    private val user: String = "sa",
    private val password: String = "",
) {
    fun getConnection(): Connection = DriverManager.getConnection(url, user, password)
}

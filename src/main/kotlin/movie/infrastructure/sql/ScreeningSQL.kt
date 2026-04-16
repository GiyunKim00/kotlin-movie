package movie.infrastructure.sql

object ScreeningSQL {
    const val INSERT_MOVIE  = """
        insert into movies(title, running_time)
        values (?, ?)
    """

    const val INSERT_SCREENINGS = """
        insert into screenings(movie_id, start_time)
        values (?, ?)
    """

    const val INSERT_RESERVED_SEAT = """
        insert into reserved_seats(screening_id, seat_row, seat_column)
        values (?, ?, ?)
    """

    const val HAS_MOVIE = """
        select count(*) from movies
    """
}
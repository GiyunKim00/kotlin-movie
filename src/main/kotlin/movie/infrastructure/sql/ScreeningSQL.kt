package movie.infrastructure.sql

object ScreeningSQL {
    const val INSERT_MOVIE = """
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

    const val FIND_BY_MOVIE_TITLE_AND_DATE = """
        select
            s.id as screening_id,
            s.start_time,
            m.id as movie_id,
            m.title,
            m.running_time
        from screenings s
        join movies m on s.movie_id = m.id
        where m.title = ?
          and cast(s.start_time as date) = ?
        order by s.start_time
    """

    const val FIND_BY_SCREENING_ID = """
        select
            s.id as screening_id,
            s.start_time,
            m.id as movie_id,
            m.title,
            m.running_time
        from screenings s
        join movies m on s.movie_id = m.id
        where s.id = ?
    """

    const val FIND_RESERVED_SEATS_BY_SCREENING_ID = """
        select seat_row, seat_column
        from reserved_seats
        where screening_id = ?
        order by seat_row, seat_column
    """

    const val FIND_SCREENINGS_WITH_MOVIES = """
        select
            m.id as movie_id,
            m.title,
            m.running_time,
            s.id as screening_id,
            s.start_time
        from movies m
        join screenings s on s.movie_id = m.id
        order by m.id, s.start_time
    """
}
package movie.infrastructure.sql

object InitializerSQL {
    const val CREATE_MOVIES_TABLE = """
        create table if not exists movies (
            id bigint auto_increment primary key,
            title varchar(255) not null,
            running_time int not null
        )
    """

    const val CREATE_SCREENINGS_TABLE = """
        create table if not exists screenings (
            id bigint auto_increment primary key,
            movie_id bigint not null,
            start_time timestamp not null,
            foreign key (movie_id) references movies(id)
        )
    """

    const val CREATE_RESERVED_SEATS_TABLE = """
        create table if not exists reserved_seats (
            id bigint auto_increment primary key,
            screening_id bigint not null,
            seat_row varchar(10) not null,
            seat_column int not null,
            foreign key (screening_id) references screenings(id),
            unique (screening_id, seat_row, seat_column)
        )
    """
}

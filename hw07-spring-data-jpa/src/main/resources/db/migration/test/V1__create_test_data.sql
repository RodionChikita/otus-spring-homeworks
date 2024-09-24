create table
authors(
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

create table
genres(
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table
books(
    id bigserial,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);

create table
books_genres(
    book_id bigint references books(id) on delete cascade,
    genre_id bigint references genres(id) on delete cascade,
    primary key (book_id, genre_id)
);

create table
comments(
    id bigserial,
    book_id bigint references books(id) on delete cascade,
    text varchar(255),
    primary key (id)
);

insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');
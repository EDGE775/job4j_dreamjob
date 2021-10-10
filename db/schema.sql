CREATE TABLE post
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE city
(
    id   SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE candidate
(
    id      SERIAL PRIMARY KEY,
    name    TEXT,
    created timestamp,
    city_id int references city (id)
);

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    name     TEXT,
    email    TEXT unique,
    password TEXT
);

insert into city (name) values ('Москва');
insert into city (name) values ('Люберцы');
insert into city (name) values ('Сочи');
insert into city (name) values ('Челябинск');
insert into city (name) values ('Новосибирск');
insert into city (name) values ('Санкт-Петербург');
insert into city (name) values ('Владивосток');
insert into city (name) values ('Оренбург');
insert into city (name) values ('Кемерово');
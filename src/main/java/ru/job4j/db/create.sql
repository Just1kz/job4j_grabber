DROP DATABASE IF EXISTS grabber;
CREATE DATABASE grabber;

create table grabber.public.post (
        id serial primary key,
        name text,
        body text unique,
        link text,
        created varchar(255)
)


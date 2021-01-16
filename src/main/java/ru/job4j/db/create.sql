DROP DATABASE IF EXISTS grabber;
CREATE DATABASE grabber;

create table if not exists grabber.public.post (
        id serial primary key,
        name text unique not null,
        body text,
        link text,
        created_dateWithTime text
)


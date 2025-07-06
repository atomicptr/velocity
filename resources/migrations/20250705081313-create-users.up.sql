create table users (
    id integer primary key autoincrement not null,
    email varchar(255) unique not null,
    password varchar(255) not null,
    name varchar(255),
    email_verified_at timestamp,
    created_at timestamp not null,
    updated_at timestamp not null
);
--;;
create table password_reset_tokens (
    email varchar(255) primary key not null references users(email),
    token varchar(255) not null,
    created_at timestamp
);
--;;
create table sessions (
    session_id varchar(255) primary key not null,
    data text,
    created_at timestamp not null,
    updated_at timestamp not null
);

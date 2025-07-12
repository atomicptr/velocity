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
create table email_change_request (
    user_id integer primary key references users(id),
    email varchar(255) not null,
    token varchar(255) not null,
    created_at timestamp
);
--;;
create table sessions (
    session_id varchar(255) primary key not null,
    user_id integer references users(id),
    data text,
    ip_address varchar(45),
    user_agent text,
    created_at timestamp not null,
    updated_at timestamp not null
);
--;;
create table email_queue (
    id integer primary key autoincrement not null,
    recipient varchar(255) not null,
    subject varchar(255) not null,
    body text not null,
    created_at timestamp not null
);

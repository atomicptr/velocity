-- :name find-user-by-id :? :1
select * from users where id = :id

-- :name find-user-by-email :? :1
select * from users where email = :email

-- :name insert-user! :! :n
insert into users (email, password, created_at, updated_at)
values (:email, :password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)

-- :name upsert-reset-password-token! :! :n
insert into password_reset_tokens (email, token, created_at)
values (:email, :token, CURRENT_TIMESTAMP)
on conflict(email) do update set token=:token, created_at=CURRENT_TIMESTAMP

-- :name find-password-reset-token :? :1
select * from password_reset_tokens where token = :token

-- :name update-password-via-token! :! :n
update users set password = :password
where email in (select email from password_reset_tokens where token = :token)

-- :name delete-reset-token! :! :n
delete from password_reset_tokens where token = :token

-- :name verify-email! :! :n
update users set email_verified_at=CURRENT_TIMESTAMP
where email = :email and email_verified_at is null

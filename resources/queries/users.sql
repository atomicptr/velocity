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
on conflict(email) do update set token=:token, created_at = CURRENT_TIMESTAMP

-- :name find-password-reset-token :? :1
select * from password_reset_tokens where token = :token

-- :name update-password-via-id! :! :n
update users
set password = :password, updated_at = CURRENT_TIMESTAMP
where id = :id

-- :name update-password-via-token! :! :n
update users set password = :password, updated_at = CURRENT_TIMESTAMP
where email in (select email from password_reset_tokens where token = :token)

-- :name update-email! :! :n
update users set email = :email, updated_at = CURRENT_TIMESTAMP where id = :user-id

-- :name update-name! :! :n
update users set name = :name, updated_at = CURRENT_TIMESTAMP where id = :user-id

-- :name delete-reset-token! :! :n
delete from password_reset_tokens where token = :token

-- :name verify-email! :! :n
update users set email_verified_at = CURRENT_TIMESTAMP
where email = :email and email_verified_at is null

-- :name create-email-change-request! :! :n
insert into email_change_request (user_id, email, token, created_at)
values (:user-id, :new-email, :token, CURRENT_TIMESTAMP)
on conflict(user_id) do update set email = :new-email, token = :token, created_at = CURRENT_TIMESTAMP

-- :name find-email-change-request :? :1
select * from email_change_request where user_id = :user-id and token = :token

-- :name delete-email-change-request! :! :n
delete from email_change_request where user_id = :user-id

-- :name clean-email-change-requests! :! :n
delete from email_change_request where created_at <= DATETIME(CURRENT_TIMESTAMP, '-' || :older-than-secs || ' seconds')

-- :name clean-password-reset-tokens! :! :n
delete from password_reset_tokens where created_at <= DATETIME(CURRENT_TIMESTAMP, '-' || :older-than-secs || ' seconds')

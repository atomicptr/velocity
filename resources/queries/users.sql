-- :name find-user-by-email :? :1
select * from users where email = :email

-- :name insert-user! :! :n
insert into users (email, password, created_at, updated_at)
values (:email, :password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)

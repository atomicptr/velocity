-- :name find-user-by-email :? :1
select * from users where email = :email

-- :name insert-user :! :n
insert into users (email, password, created_at, updated_at)
values (:email, :password, :created-at, :updated-at)

-- :name create-session :! :n
insert into sessions (session_id, user_id, ip_address, user_agent, last_activity, created_at)
values (:session-id, :user-id, :ip, :user-agent, :last-activity, :created-at)

-- :name find-session :? :1
select * from sessions where session_id = :session-id


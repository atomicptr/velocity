-- :name update-session-data :! :n
insert into sessions (session_id, user_id, data, created_at, updated_at)
values (:session-id, :user-id, :data, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
on conflict(session_id) do update set user_id=:user-id, data=:data, updated_at=CURRENT_TIMESTAMP

-- :name get-session-data :? :1
select data from sessions where session_id = :session-id

-- :name delete-session :! :n
delete from sessions where session_id = :session-id

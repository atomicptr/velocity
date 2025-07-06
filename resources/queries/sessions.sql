-- :name update-session-data :! :n
insert into sessions (session_id, data, created_at, updated_at)
values (:session-id, :data, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
on conflict(session_id) do update set data=:data, updated_at=CURRENT_TIMESTAMP

-- :name get-session-data :? :1
select data from sessions where session_id = :session-id

-- :name delete-session :! :n
delete from sessions where session_id = :session-id

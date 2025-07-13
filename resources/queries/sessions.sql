-- :name upsert-session-data! :! :n
insert into sessions (session_id, user_id, data, ip_address, user_agent, created_at, updated_at)
values (:session-id, :user-id, :data, :ip, :user-agent, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
on conflict(session_id) do
update set
    user_id=:user-id,
    data=:data,
    ip_address=COALESCE(:ip, sessions.ip_address),
    user_agent=COALESCE(:user-agent, sessions.user_agent),
    updated_at=CURRENT_TIMESTAMP

-- :name get-session-data :? :1
select data from sessions where session_id = :session-id

-- :name delete-session! :! :n
delete from sessions where session_id = :session-id

-- :name find-sessions :?
select * from sessions where user_id = :user-id

-- :name purge-other-sessions! :! :n
delete from sessions where user_id = :user-id and session_id != :session-id

-- :name clean-old-sessions! :! :n
delete from sessions where created_at <= DATETIME(CURRENT_TIMESTAMP, '-' || :older-than-secs || ' seconds')

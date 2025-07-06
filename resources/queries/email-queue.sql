-- :name fetch-emails :?
select * from email_queue order by created_at asc limit 100

-- :name insert-email! :! :n
insert into email_queue (recipient, subject, body, created_at)
values (:recipient, :subject, :body, CURRENT_TIMESTAMP)

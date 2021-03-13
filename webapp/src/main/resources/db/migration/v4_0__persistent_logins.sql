-- Creation Date: 2021-03-11
-- Description:
-- - add column user_id to persistent logins table

truncate table persistent_logins;

alter table persistent_logins add column user_id bigint not null default 0;

alter table persistent_logins alter column user_id drop default;

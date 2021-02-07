-- Creation Date: 2021-02-06
-- Description: users table now contains information on the user being a normal
--              user or an oauth user; avatar column added

alter table users rename to abstract_users;

alter table users_user_roles rename to abstract_users_user_roles;

alter table abstract_users add column dtype varchar(255) not null default 'users';

alter table abstract_users alter column dtype drop default;

alter table abstract_users alter column encrypted_password drop not null;

alter table abstract_users add column avatar varchar(255);

alter table abstract_users_user_roles rename column users_id to abstract_users_id;

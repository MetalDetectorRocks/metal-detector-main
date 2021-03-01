-- Creation Date: 2021-02-06
-- Description: users table now contains information on the user being a normal
--              user or an oauth user; avatar column added; unique constraint
--              for username dropped

alter table users add column dtype varchar(255) not null default 'basic_users';

alter table users alter column dtype drop default;

alter table users alter column encrypted_password drop not null;

alter table users add column avatar varchar(255);

alter table users drop constraint uk_r43af9ap4edm43mmtq01oddj6;

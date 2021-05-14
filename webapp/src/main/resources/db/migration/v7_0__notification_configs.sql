-- Creation Date: 2021-05-13
-- Description: multiple notification configs with separate telegram config

alter table notification_configs add column channel varchar(255) not null default 'EMAIL';

alter table notification_configs drop column telegram_chat_id, drop column telegram_registration_id;

create table telegram_configs(
    id bigserial not null constraint telegram_configs_pkey primary key,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    chat_id integer,
    registration_id integer,
    notification_configs_id bigint not null constraint fktbume816qt0bpad3goy3gw8aq references notification_configs
);

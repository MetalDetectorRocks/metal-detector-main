-- Creation Date: 2022-10-30
-- Description: create refresh tokens table

create table refresh_tokens (
    id bigserial not null constraint refresh_tokens_pkey primary key,
    token text,
    users_id bigint,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

alter table refresh_tokens add constraint UK_ghpmfn23vmxfu3spu3lfg4r2d unique (token);
alter table refresh_tokens add constraint FK1lih5y2npsf8u5o3vhdb9y0os foreign key (users_id) references users;

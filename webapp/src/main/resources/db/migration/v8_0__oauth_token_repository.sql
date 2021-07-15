-- Creation Date: 2021-07-15
-- Description: table for oauth tokens

create table oauth2_authorized_client(
    client_registration_id varchar(100) not null,
    principal_name varchar(200) not null,
    access_token_type varchar(100) not null,
    access_token_value bytea not null,
    access_token_issued_at timestamp not null,
    access_token_expires_at timestamp not null,
    access_token_scopes varchar(1000) default null,
    refresh_token_value bytea default null,
    refresh_token_issued_at timestamp default null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    primary key (client_registration_id, principal_name)
);

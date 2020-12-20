-- Creation Date: 2020-12-20
-- Description: Creates the initial tables

create table artists (
    id bigserial not null constraint artists_pkey primary key,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    artist_name varchar(255) not null,
    external_id varchar(255) not null,
    source varchar(255) not null,
    thumb varchar(255)
);

create table users (
    id bigserial not null constraint users_pkey primary key,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    account_non_expired boolean,
    account_non_locked boolean,
    credentials_non_expired boolean,
    email varchar(120) not null constraint uk_6dotkott2kjsp8vw4d0m25fb7 unique,
    enabled boolean,
    last_login timestamp,
    encrypted_password varchar(60) not null,
    public_id varchar(255) not null constraint uk_s24bux761rbgowsl7a4b386ba unique,
    username varchar(50) not null constraint uk_r43af9ap4edm43mmtq01oddj6 unique
);

create table follow_actions (
    id bigserial not null constraint follow_actions_pkey primary key,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    artist_id bigint not null constraint fkq5glujenmhkt2eq1tlyoap5a7 references artists,
    user_id bigint not null constraint fk6vjwo91b8unvo92h00pe6oarb references users
);

create table spotify_authorizations (
    id bigserial not null constraint spotify_authorizations_pkey primary key,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    access_token varchar(255),
    expires_in integer,
    refresh_token varchar(255),
    scope varchar(255),
    state varchar(255) not null,
    token_type varchar(255),
    users_id bigint not null constraint fkftqvlgssfh6fspdpgswnb2c2u references users,
    expires_at timestamp
);

create table tokens (
    id bigserial not null constraint tokens_pkey primary key,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    expiration_date_time timestamp not null,
    token_string text not null,
    token_type varchar(255) not null,
    users_id bigint not null constraint fkl50lok37qf2734u04knlj57ct references users
);

create table users_user_roles (
    users_id bigint not null constraint fkor54r63fqp9kww8u2b4t1hqtl references users,
    user_roles varchar(255) not null,
    constraint users_user_roles_pkey primary key (users_id, user_roles)
);

create table persistent_logins (
    username varchar(100) not null,
    series varchar(64) not null constraint persistent_logins_pkey primary key,
    token varchar(64) not null,
    last_used timestamp not null
);

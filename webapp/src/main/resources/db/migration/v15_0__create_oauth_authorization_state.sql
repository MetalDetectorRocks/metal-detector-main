-- Creation Date: 2024-02-10
-- Description: create oauth authorization state table

create table oauth_authorization_states (
    id bigserial not null constraint oauth_authorization_state_pkey primary key,
    state varchar(100),
    users_id bigint not null references users,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

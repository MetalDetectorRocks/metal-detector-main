-- Creation Date: 2021-08-29
-- Description: make reissue notifications configurable

alter table notification_configs add column notify_reissues boolean default false not null;

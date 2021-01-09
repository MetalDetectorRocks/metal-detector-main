-- Creation Date: 2021-01-07
-- Description: Adds columns lastNotificationDate to table notification_configs;
--              removes unnecessary column in table spotify_authorizations

alter table notification_configs add column last_notification_date date;

alter table spotify_authorizations drop column expires_in;

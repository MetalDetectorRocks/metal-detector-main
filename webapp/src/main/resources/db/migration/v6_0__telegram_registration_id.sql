-- Creation Date: 2021-04-06
-- Description: telegram registration id added to notification config

alter table notification_configs add column telegram_registration_id integer;

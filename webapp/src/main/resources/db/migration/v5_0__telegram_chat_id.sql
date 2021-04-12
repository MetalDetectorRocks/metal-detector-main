-- Creation Date: 2021-04-06
-- Description: telegram chat id added to notification config

alter table notification_configs add column telegram_chat_id integer;

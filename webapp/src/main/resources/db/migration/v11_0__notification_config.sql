-- Creation Date: 2021-09-11
-- Description: remove notify column from notification config

alter table notification_configs drop column if exists notify;

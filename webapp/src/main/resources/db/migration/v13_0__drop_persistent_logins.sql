-- Creation Date: 2022-10-30
-- Description: remove persistent_logins, because remember me is implemented with refresh tokens

drop table persistent_logins;

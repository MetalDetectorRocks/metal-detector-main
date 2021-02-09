-- Creation Date: 2021-02-07
-- Description:
-- - adds new columns: external_url, external_uri, genres, spotify_popularity, spotify_follower, image_xs, image_s, image_m
-- - copies value from column thumb to image_xs, image_s, image_m
-- - renames column thumb to image_l

alter table artists
add column external_url varchar(255),
add column external_uri varchar(255),
add column genres varchar(255),
add column spotify_popularity integer,
add column spotify_follower integer,
add column image_xs varchar(255),
add column image_s varchar(255),
add column image_m varchar(255);

update artists
set image_xs = thumb,
    image_s = thumb,
    image_m = thumb
where thumb is not null;

alter table artists
rename column thumb TO image_l;

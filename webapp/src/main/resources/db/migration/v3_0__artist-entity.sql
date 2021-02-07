-- Creation Date: 2021-02-07
-- Description: Renames column 'thumb' to image_l
--              adds new columns: external_url, genres, spotify_popularity, spotify_follower, image_xs, image_s, image_m

alter table artists
rename column thumb TO image_l;

alter table artists
add column external_url varchar(255),
add column genres varchar(255),
add column spotify_popularity integer,
add column spotify_follower integer,
add column image_xs varchar(255),
add column image_s varchar(255),
add column image_m varchar(255);

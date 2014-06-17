-- If you are running locally, make the database and the account
-- If you are in a hosted environment, you usually get a 
-- dtabase name, account and password - so skip ahead
-- to creating the tables

create database ads;
grant all on ads.* to adsuser@'localhost' identified by 'adspassword';
grant all on ads.* to adsuser@'127.0.0.1' identified by 'adspassword';

use ads;

-- Make the tables

drop table if exists blti_keys;
create table blti_keys (
     id          MEDIUMINT NOT NULL AUTO_INCREMENT,
     oauth_consumer_key   CHAR(255) NOT NULL,
     secret      CHAR(255) NULL,
     name        CHAR(255) NULL,
     context_id  CHAR(255) NULL,
     created_at  DATETIME NOT NULL,
     updated_at  DATETIME NOT NULL,
     PRIMARY KEY (id)
 );

drop table if exists ads;
create table ads (
     id          MEDIUMINT NOT NULL AUTO_INCREMENT,
     course_key  CHAR(255) NOT NULL,
     user_key    CHAR(255) NULL,
     user_name   CHAR(255) NULL,
     user_image  TEXT(1024) NULL,
     title       CHAR(255) NULL,
     description     TEXT(2048) NULL,
     created_at  DATETIME NOT NULL,
     updated_at  DATETIME NOT NULL,
     PRIMARY KEY (id)
 );

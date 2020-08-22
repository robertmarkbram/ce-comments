DROP DATABASE IF EXISTS comments;
DROP USER IF EXISTS 'comments-user'@'localhost';

CREATE DATABASE comments;
# noinspection SpellCheckingInspection
CREATE USER 'comments-user'@'localhost' IDENTIFIED WITH mysql_native_password BY '7#@aO*&W^u*8C8T29HyK7foOqd$euzi2jFc5SgP#';
GRANT ALL PRIVILEGES ON comments.* TO 'comments-user'@'localhost';

USE `comments`;


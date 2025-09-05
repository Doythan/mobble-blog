SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE board_tb;
TRUNCATE TABLE category_tb;
TRUNCATE TABLE user_tb;

ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1;
ALTER TABLE category_tb ALTER COLUMN id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;

-- 샘플 데이터
INSERT INTO user_tb (username, password, email, profile_image)
VALUES ('ssar', '1234', 'ssar@test.com', 'default.png');
INSERT INTO user_tb (username, password, email, profile_image)
VALUES ('cos', '1234', 'cos@test.com', 'default.png');
INSERT INTO user_tb (username, password, email, profile_image)
VALUES ('love', '1234', 'love@test.com', 'default.png');

INSERT INTO category_tb (category, user_id)
VALUES ('Java', 1), ('Algorithm', 1), ('Flutter', 2), ('Python', 3);
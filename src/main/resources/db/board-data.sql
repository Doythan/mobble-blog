INSERT INTO board_tb(title, content, user_id, views, category_id, created_at, updated_at)
VALUES
    ('제목1', '내용1', 1, 1, 1, CURRENT_TIMESTAMP - INTERVAL '1' HOUR, CURRENT_TIMESTAMP),
    ('제목2', '내용2', 2, 10, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('제목3', '내용3', 3, 100, 3, CURRENT_TIMESTAMP - INTERVAL '1' HOUR, CURRENT_TIMESTAMP),
    ('제목4', '내용4', 1, 200, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('제목5', '내용5', 2, 300, 2, CURRENT_TIMESTAMP - INTERVAL '1' HOUR, CURRENT_TIMESTAMP);



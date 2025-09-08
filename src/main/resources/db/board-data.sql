insert into board_tb(title, content, user_id, views, category_id, created_at, updated_at)
values ('제목1', '내용1', 1, 1, 1, DATEADD('HOUR', -1, NOW()), now()),
       ('제목2', '내용2', 2, 10, 2, DATEADD('MINUTE', -30, NOW()), null),
       ('제목3', '내용3', 3, 100, 3, DATEADD('HOUR', -3, NOW()), now()),
       ('제목4', '내용4', 1, 200, 1, DATEADD('MINUTE', -90, NOW()), null),
       ('제목5', '내용5', 2, 300, 2, DATEADD('HOUR', -2, NOW()), now());
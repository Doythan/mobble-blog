INSERT INTO report_tb(board_id, user_id, result, result_etc, content, status, created_at)
VALUES
-- 광고성 글 신고 (ssar이 작성자 cos 글을 신고)
(2, 1, 'ADVERTISING_BOARD_CONTENT', NULL, '광고성 글이라 신고합니다.', 'PENDING', NOW()),

-- 욕설/비방 신고 (cos이 user1 글을 신고)
(3, 2, 'ABUSIVE_LANGUAGE', NULL, '댓글에 심한 욕설이 있습니다.', 'PENDING', NOW()),

-- 개인정보 노출 신고 (user1이 ssar 글을 신고)
(1, 3, 'PERSONAL_INFORMATION', NULL, '전화번호가 노출되어 있습니다.', 'PROCESSING', NOW()),

-- 기타 신고 (cos이 ssar 글을 신고, 직접 사유 입력)
(4, 2, 'ETC', '정치 관련 발언', '부적절한 정치 발언이 있어 신고합니다.', 'PENDING', NOW()),

-- 도배/스팸 신고 (user1이 cos 글을 신고)
(5, 1, 'SPAM', NULL, '같은 내용이 반복적으로 작성되었습니다.', 'COMPLETED', NOW());
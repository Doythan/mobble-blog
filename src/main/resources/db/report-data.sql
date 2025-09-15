INSERT INTO report_tb(board_id, user_id, result, result_etc, content, status, created_at)
VALUES
-- 광고성 글 신고 (board_id 2, 7, 12)
(2, 1, 'ADVERTISING_BOARD_CONTENT', NULL, '광고성 글이라 신고합니다.', 'PENDING', NOW()),
(7, 5, 'ADVERTISING_BOARD_CONTENT', NULL, '쇼핑몰 홍보성 글입니다.', 'PENDING', NOW()),
(12, 9, 'ADVERTISING_BOARD_CONTENT', NULL, '불법 광고가 포함되어 있습니다.', 'PENDING', NOW()),

-- 욕설/비방 신고 (board_id 3, 8, 13)
(3, 2, 'ABUSIVE_LANGUAGE', NULL, '댓글에 심한 욕설이 있습니다.', 'PENDING', NOW()),
(8, 6, 'ABUSIVE_LANGUAGE', NULL, '비방성 발언이 있어 신고합니다.', 'PENDING', NOW()),
(13, 10, 'ABUSIVE_LANGUAGE', NULL, '욕설이 반복적으로 등장합니다.', 'COMPLETED', NOW()),

-- 개인정보 노출 신고 (board_id 4, 9, 14)
(4, 3, 'PERSONAL_INFORMATION', NULL, '전화번호가 노출되어 있습니다.', 'COMPLETED', NOW()),
(9, 7, 'PERSONAL_INFORMATION', NULL, '개인 이메일이 노출되어 있습니다.', 'PENDING', NOW()),
(14, 1, 'PERSONAL_INFORMATION', NULL, '주민번호 일부가 노출되었습니다.', 'PENDING', NOW()),

-- 기타 신고 (board_id 5, 10, 15)
(5, 4, 'ETC', '정치 관련 발언', '부적절한 정치 발언이 있어 신고합니다.', 'PENDING', NOW()),
(10, 8, 'ETC', '종교 비하', '종교를 비하하는 발언이 있어 신고합니다.', 'PENDING', NOW()),
(15, 2, 'ETC', '허위 정보', '사실과 다른 허위 정보가 있습니다.', 'REJECTED', NOW()),

-- 도배/스팸 신고 (board_id 6, 11, 16)
(6, 5, 'SPAM', NULL, '같은 내용이 반복적으로 작성되었습니다.', 'COMPLETED', NOW()),
(11, 9, 'SPAM', NULL, '의미 없는 반복 글입니다.', 'PENDING', NOW()),
(16, 3, 'SPAM', NULL, '스팸성 홍보 글입니다.', 'PENDING', NOW()),

-- 추가 분산 (총 20개를 맞추기 위해)
(17, 6, 'ADVERTISING_BOARD_CONTENT', NULL, '광고 링크가 포함되어 있습니다.', 'PENDING', NOW()),
(18, 7, 'ABUSIVE_LANGUAGE', NULL, '모욕적인 표현이 있습니다.', 'PENDING', NOW()),
(19, 8, 'PERSONAL_INFORMATION', NULL, '주소가 노출되어 있습니다.', 'REJECTED', NOW()),
(20, 10, 'ETC', '선정적 표현', '성적으로 부적절한 표현이 있습니다.', 'PENDING', NOW()),
(21, 4, 'SPAM', NULL, '무의미한 도배 글입니다.', 'PENDING', NOW());
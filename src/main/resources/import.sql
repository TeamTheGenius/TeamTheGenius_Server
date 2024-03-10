# 단독 실행 시 정상 작동하지만, ddl-auto를 create로 해놓고 실행하면 오류 발생
INSERT INTO todoffin.item (cost, created_at, deleted_at, updated_at, details, name, item_category)
VALUES (100, NULL, NULL, NULL, '프로필 프레임', 'profile frame', 'PROFILE_FRAME'),
       (100, NULL, NULL, NULL, '인증 패스 아이템', 'certification passer', 'CERTIFICATION_PASSER'),
       (100, NULL, NULL, NULL, '포인트 2배 획득 아이템', 'point multiplier', 'POINT_MULTIPLIER');

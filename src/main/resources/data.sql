INSERT INTO item (cost, created_at, deleted_at, updated_at, details, name, item_category)
SELECT *
FROM (SELECT 100                    AS cost,
             NULL                   AS created_at,
             NULL                   AS deleted_at,
             NULL                   AS updated_at,
             '프로필을 꾸밀 수 있는 프레임입니다.' AS details,
             '프로필 프레임'              AS name,
             'PROFILE_FRAME'        AS item_category
      UNION ALL
      SELECT 100, NULL, NULL, NULL, '오늘의 인증을 넘길 수 있는 아이템입니다.', '인증 패스 아이템', 'CERTIFICATION_PASSER'
      UNION ALL
      SELECT 100, NULL, NULL, NULL, '포인트 보상을 2배로 받을 수 있는 아이템입니다.', '포인트 2배 획득 아이템', 'POINT_MULTIPLIER') AS new_items
WHERE (SELECT COUNT(*) FROM item) < 3;

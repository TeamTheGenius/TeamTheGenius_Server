INSERT INTO item (cost, created_at, deleted_at, updated_at, details, name, item_category)
SELECT *
FROM (SELECT 100             AS cost,
             NULL            AS created_at,
             NULL            AS deleted_at,
             NULL            AS updated_at,
             '프로필 프레임'       AS details,
             'profile frame' AS name,
             'PROFILE_FRAME' AS item_category
      UNION ALL
      SELECT 100, NULL, NULL, NULL, '인증 패스 아이템', 'certification passer', 'CERTIFICATION_PASSER'
      UNION ALL
      SELECT 100, NULL, NULL, NULL, '포인트 2배 획득 아이템', 'point multiplier', 'POINT_MULTIPLIER') AS new_items
WHERE (SELECT COUNT(*) FROM item) < 3;

INSERT INTO item (identifier, cost, details, name, item_category)
SELECT *
FROM (SELECT 1                      AS identifier,
             100                    AS cost,
             '프로필을 꾸밀 수 있는 프레임입니다.' AS details,
             '성탄절 프레임'              AS name,
             'PROFILE_FRAME'        AS item_category
      UNION ALL
      SELECT 2,
             100,
             '프로필을 꾸밀 수 있는 프레임입니다.',
             '어둠의 힘 프레임',
             'PROFILE_FRAME'
      UNION ALL
      SELECT 3,
             100,
             '오늘의 인증을 넘길 수 있는 아이템입니다.',
             '인증 패스권',
             'CERTIFICATION_PASSER'
      UNION ALL
      SELECT 4,
             100,
             '아이템 사용 시, 챌린지 성공 보상을 2배로 획득할 수 있는 아이템입니다.',
             '챌린지 보상 획득 2배 아이템',
             'POINT_MULTIPLIER'

      UNION ALL
      SELECT 5,
             100,
             '프로필을 꾸밀 수 있는 프레임입니다.',
             '불태워라 프레임',
             'PROFILE_FRAME'
      UNION ALL
      SELECT 6,
             100,
             '프로필을 꾸밀 수 있는 프레임입니다.',
             '끈적이는 프레임',
             'PROFILE_FRAME'
      UNION ALL
      SELECT 7,
             100,
             '프로필을 꾸밀 수 있는 프레임입니다.',
             '무섭지롱 프레임',
             'PROFILE_FRAME') AS new_items
WHERE (SELECT COUNT(*) FROM item) < 3;

INSERT INTO users (`point`, user_id, nickname, information, identifier, tags, provider_info, `role`)
SELECT 0,
       104,
       'Guest',
       '자기 소개입니다.',
       'Guest',
       'Java,Spring',
       'GITHUB',
       'USER'
WHERE NOT EXISTS (SELECT 1
                  FROM users
                  WHERE identifier = 'Guest');

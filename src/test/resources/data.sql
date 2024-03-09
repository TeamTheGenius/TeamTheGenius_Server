# User insert문
insert into user (created_at, deleted_at, files_id, point, updated_at, nickname, information, identifier, tags,
                  provider_info, role)
values ('2023-03-04 11:01:48.594485', null, null, 1500, null, 'dohyungKim', '저는 백엔드 개발자입니다.', 'kimdozzi', 'BE, CS',
        'GITHUB', 'ADMIN');
insert into user (created_at, deleted_at, files_id, point, updated_at, nickname, information, identifier, tags,
                  provider_info, role)
values ('2023-03-02 20:14:10.594123', null, null, 2500, null, 'yujinKim', '안녕하세요~ 저는 김유진이라고 합니다 잘부탁드려요 ^^.', 'yujin',
        'BE, CS, AI',
        'GITHUB', 'USER');
insert into user (created_at, deleted_at, files_id, point, updated_at, nickname, information, identifier, tags,
                  provider_info, role)
values ('2023-03-01 07:01:00.600000', null, null, 50000, null, 'minsuPark', '', 'minsu', 'FE',
        'GITHUB', 'NOT_REGISTERED');


# Topic insert문
insert into topic (point_per_person, created_at, deleted_at, files_id, updated_at, description, notice, tags, title)
values (100, null, null, null, null, '1일 1알고리즘 챌린지입니다.', '85%이상 참여 시 포인트가 발행됩니다.', 'BE', '1일 1알고리즘 챌린지');


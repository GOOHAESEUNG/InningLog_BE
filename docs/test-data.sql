-- ============================================
-- 직관일지 & 좌석후기 테스트 데이터
-- member_id = 1 (LG 팬 기준)
-- ============================================
-- 팀 ID 참조: 1=KIA, 2=삼성, 3=LG, 4=두산, 5=KT, 6=SSG, 7=롯데, 8=한화, 9=NC, 10=키움
-- 구장 ID 참조: 1=잠실, 2=고척, 3=문학, 4=수원, 5=대전, 6=대구, 7=사직, 8=창원, 9=광주
-- ============================================


-- ============================================
-- 0단계: 기존 데이터 삭제 (FK 의존성 역순)
-- ============================================

-- 좌석후기 감정태그 매핑 삭제
DELETE FROM seat_view_emotion_tag_map
WHERE seat_view_id IN (SELECT id FROM seat_view WHERE memebr_id = 1);

-- 좌석후기/직관일지 관련 이미지 삭제
DELETE FROM content_image
WHERE content_type = 'SEATVIEW' AND target_id IN (SELECT id FROM seat_view WHERE memebr_id = 1);

DELETE FROM content_image
WHERE content_type = 'JOURNAL' AND target_id IN (SELECT id FROM journal WHERE member_id = 1);

-- journal → seat_view FK 해제
UPDATE journal SET seat_view_id = NULL WHERE member_id = 1;

-- 좌석후기 삭제
DELETE FROM seat_view WHERE memebr_id = 1;

-- 직관일지 삭제
DELETE FROM journal WHERE member_id = 1;

-- AUTO_INCREMENT 리셋 (테이블이 비었을 때만 유효)
ALTER TABLE journal AUTO_INCREMENT = 1;
ALTER TABLE seat_view AUTO_INCREMENT = 1;


-- ============================================
-- 1단계: 직관일지 (Journal) 10건
-- ============================================
INSERT INTO journal (member_id, date, opponent_team_id, our_score, their_score, result_score, emotion, review_text, stadium_id, is_public, comment_count, like_count, scrap_count, created_at, updated_at)
VALUES
-- #1 잠실 vs 두산 승리
(1, '2025-04-05 14:00:00', 4, 5, 3, 'WIN', 'EXCITED',
 '시즌 첫 직관 승리! 오스틴 쓰리런 홈런에 잠실이 들썩였다. 봄 햇살 아래 최고의 개막 직관.',
 1, true, 0, 0, 0, '2025-04-05 18:00:00', '2025-04-05 18:00:00'),

-- #2 잠실 vs KT 패배
(1, '2025-04-12 14:00:00', 5, 2, 4, 'LOSE', 'FRUSTRATED',
 '7회까지 리드하다가 8회에 뒤집혔다. 불펜이 아쉬운 경기. 그래도 날씨는 좋았다.',
 1, true, 0, 0, 0, '2025-04-12 18:00:00', '2025-04-12 18:00:00'),

-- #3 고척 vs 키움 무승부
(1, '2025-04-26 14:00:00', 10, 3, 3, 'DRAW', 'REGRETFUL',
 '원정 고척 직관. 9회말 동점 허용이 너무 아쉽다. 고척돔 실내라 쾌적하긴 했음.',
 2, true, 0, 0, 0, '2025-04-26 18:00:00', '2025-04-26 18:00:00'),

-- #4 문학 vs SSG 대승
(1, '2025-05-10 14:00:00', 6, 7, 1, 'WIN', 'SATISFIED',
 '원정 문학 가서 대승! 타선이 폭발했다. 인천 바다바람이 시원해서 좋았음.',
 3, true, 0, 0, 0, '2025-05-10 18:00:00', '2025-05-10 18:00:00'),

-- #5 잠실 vs 삼성 완패
(1, '2025-05-24 18:30:00', 2, 1, 5, 'LOSE', 'ANGRY',
 '선발이 초반에 무너졌다. 응원도 힘 빠지는 경기였음. 야식으로 화풀이.',
 1, false, 0, 0, 0, '2025-05-24 22:00:00', '2025-05-24 22:00:00'),

-- #6 대전 vs 한화 역전승
(1, '2025-06-07 14:00:00', 8, 6, 4, 'WIN', 'TOUCHED',
 '5회까지 지고 있다가 7회에 대역전! 원정 응원단 분위기 최고. 감동적인 경기.',
 5, true, 0, 0, 0, '2025-06-07 18:00:00', '2025-06-07 18:00:00'),

-- #7 잠실 vs 롯데 승리 (야간)
(1, '2025-06-21 18:30:00', 7, 4, 2, 'WIN', 'EXCITED',
 '잠실 야간 직관. 시원한 저녁 바람에 맥주 한잔. 홍창기 결승타에 환호!',
 1, true, 0, 0, 0, '2025-06-21 22:00:00', '2025-06-21 22:00:00'),

-- #8 사직 vs 롯데 패배
(1, '2025-07-12 18:30:00', 7, 3, 6, 'LOSE', 'REGRETFUL',
 '부산 원정 직관. 사직 분위기는 역시 대단하다. 졌지만 원정의 묘미.',
 7, true, 0, 0, 0, '2025-07-12 22:00:00', '2025-07-12 22:00:00'),

-- #9 잠실 vs NC 대승
(1, '2025-08-02 18:30:00', 9, 8, 2, 'WIN', 'SATISFIED',
 '타선 폭발! 모든 타자가 안타를 쳤다. 잠실 만석에 응원 열기가 대단했음.',
 1, true, 0, 0, 0, '2025-08-02 22:00:00', '2025-08-02 22:00:00'),

-- #10 수원 vs KT 석패
(1, '2025-08-16 14:00:00', 5, 2, 3, 'LOSE', 'FRUSTRATED',
 '수원 원정. 9회 끝내기로 져서 허탈. 수원 구장은 깔끔하고 좋았다.',
 4, false, 0, 0, 0, '2025-08-16 18:00:00', '2025-08-16 18:00:00');


-- 방금 INSERT된 journal ID 범위 조회
SET @J_START = (SELECT MIN(id) FROM journal WHERE member_id = 1);


-- ============================================
-- 2단계: 좌석후기 (SeatView) 10건
-- ============================================
INSERT INTO seat_view (memebr_id, journal_id, stadium_id, zone_id, section, seat_row, created_at, updated_at)
VALUES
(1, @J_START + 0, 1, 1, '110', '5', '2025-04-05 18:00:00', '2025-04-05 18:00:00'),
(1, @J_START + 1, 1, 3, '201', '12', '2025-04-12 18:00:00', '2025-04-12 18:00:00'),
(1, @J_START + 2, 2, 10, '115', '8', '2025-04-26 18:00:00', '2025-04-26 18:00:00'),
(1, @J_START + 3, 3, 28, '305', '3', '2025-05-10 18:00:00', '2025-05-10 18:00:00'),
(1, @J_START + 4, 1, 5, '120', '1', '2025-05-24 22:00:00', '2025-05-24 22:00:00'),
(1, @J_START + 5, 5, 60, '210', '7', '2025-06-07 18:00:00', '2025-06-07 18:00:00'),
(1, @J_START + 6, 1, 2, '108', '15', '2025-06-21 22:00:00', '2025-06-21 22:00:00'),
(1, @J_START + 7, 7, 96, '315', '10', '2025-07-12 22:00:00', '2025-07-12 22:00:00'),
(1, @J_START + 8, 1, 4, '112', '9', '2025-08-02 22:00:00', '2025-08-02 22:00:00'),
(1, @J_START + 9, 4, 49, '205', '6', '2025-08-16 18:00:00', '2025-08-16 18:00:00');


-- 방금 INSERT된 seat_view ID 범위 조회
SET @SV_START = (SELECT MIN(id) FROM seat_view WHERE memebr_id = 1);


-- ============================================
-- 3단계: Journal ↔ SeatView 연결
-- ============================================
UPDATE journal SET seat_view_id = @SV_START + 0 WHERE id = @J_START + 0;
UPDATE journal SET seat_view_id = @SV_START + 1 WHERE id = @J_START + 1;
UPDATE journal SET seat_view_id = @SV_START + 2 WHERE id = @J_START + 2;
UPDATE journal SET seat_view_id = @SV_START + 3 WHERE id = @J_START + 3;
UPDATE journal SET seat_view_id = @SV_START + 4 WHERE id = @J_START + 4;
UPDATE journal SET seat_view_id = @SV_START + 5 WHERE id = @J_START + 5;
UPDATE journal SET seat_view_id = @SV_START + 6 WHERE id = @J_START + 6;
UPDATE journal SET seat_view_id = @SV_START + 7 WHERE id = @J_START + 7;
UPDATE journal SET seat_view_id = @SV_START + 8 WHERE id = @J_START + 8;
UPDATE journal SET seat_view_id = @SV_START + 9 WHERE id = @J_START + 9;


-- ============================================
-- 4단계: 좌석후기 감정 태그 매핑
-- ID 하드코딩 대신 code로 조회
-- ============================================
INSERT INTO seat_view_emotion_tag_map (seat_view_id, seat_view_emotion_tag_id, created_at, updated_at)
VALUES
-- #1 잠실 1루 응원석: 응원-일어남, 햇빛-강함, 지붕-없음
(@SV_START + 0, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_STANDING'), NOW(), NOW()),
(@SV_START + 0, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_STRONG'), NOW(), NOW()),
(@SV_START + 0, (SELECT id FROM seat_view_emotion_tag WHERE code = 'ROOF_NONE'), NOW(), NOW()),
-- #2 잠실 외야석: 응원-일어남, 햇빛-있다가그늘짐, 좌석-좁음
(@SV_START + 1, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_STANDING'), NOW(), NOW()),
(@SV_START + 1, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_MOVES_TO_SHADE'), NOW(), NOW()),
(@SV_START + 1, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SEAT_SPACE_NARROW'), NOW(), NOW()),
-- #3 고척 1루 내야석: 응원-앉아서, 햇빛-없음, 지붕-있음
(@SV_START + 2, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_SEATED'), NOW(), NOW()),
(@SV_START + 2, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_NONE'), NOW(), NOW()),
(@SV_START + 2, (SELECT id FROM seat_view_emotion_tag WHERE code = 'ROOF_EXISTS'), NOW(), NOW()),
-- #4 문학 원정 응원석: 응원-일어남, 햇빛-강함, 시야방해-없음
(@SV_START + 3, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_STANDING'), NOW(), NOW()),
(@SV_START + 3, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_STRONG'), NOW(), NOW()),
(@SV_START + 3, (SELECT id FROM seat_view_emotion_tag WHERE code = 'VIEW_NO_OBSTRUCTION'), NOW(), NOW()),
-- #5 잠실 테이블석: 응원-앉아서, 햇빛-없음, 좌석-아주넓음
(@SV_START + 4, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_SEATED'), NOW(), NOW()),
(@SV_START + 4, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_NONE'), NOW(), NOW()),
(@SV_START + 4, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SEAT_SPACE_VERY_WIDE'), NOW(), NOW()),
-- #6 대전 원정 응원석: 응원-일어남, 햇빛-있다가그늘짐, 지붕-없음
(@SV_START + 5, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_STANDING'), NOW(), NOW()),
(@SV_START + 5, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_MOVES_TO_SHADE'), NOW(), NOW()),
(@SV_START + 5, (SELECT id FROM seat_view_emotion_tag WHERE code = 'ROOF_NONE'), NOW(), NOW()),
-- #7 잠실 3루 내야석: 응원-일어날사람은일어남, 시야방해-그물, 좌석-보통
(@SV_START + 6, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_MOSTLY_STANDING'), NOW(), NOW()),
(@SV_START + 6, (SELECT id FROM seat_view_emotion_tag WHERE code = 'VIEW_OBSTRUCT_NET'), NOW(), NOW()),
(@SV_START + 6, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SEAT_SPACE_NORMAL'), NOW(), NOW()),
-- #8 사직 원정 응원석: 응원-일어남, 햇빛-강함, 좌석-좁음
(@SV_START + 7, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_STANDING'), NOW(), NOW()),
(@SV_START + 7, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_STRONG'), NOW(), NOW()),
(@SV_START + 7, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SEAT_SPACE_NARROW'), NOW(), NOW()),
-- #9 잠실 1루 지정석: 응원-일어날사람은일어남, 햇빛-있다가그늘짐, 시야방해-없음
(@SV_START + 8, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_MOSTLY_STANDING'), NOW(), NOW()),
(@SV_START + 8, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SUN_MOVES_TO_SHADE'), NOW(), NOW()),
(@SV_START + 8, (SELECT id FROM seat_view_emotion_tag WHERE code = 'VIEW_NO_OBSTRUCTION'), NOW(), NOW()),
-- #10 수원 외야석: 응원-일어남, 지붕-없음, 좌석-넓음
(@SV_START + 9, (SELECT id FROM seat_view_emotion_tag WHERE code = 'CHEERING_STANDING'), NOW(), NOW()),
(@SV_START + 9, (SELECT id FROM seat_view_emotion_tag WHERE code = 'ROOF_NONE'), NOW(), NOW()),
(@SV_START + 9, (SELECT id FROM seat_view_emotion_tag WHERE code = 'SEAT_SPACE_WIDE'), NOW(), NOW());

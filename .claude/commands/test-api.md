# API 서버 테스트

구현한 API를 실제 서버에서 테스트합니다.

## 사용법
```
/test-api <엔드포인트들>
```

예시:
- `/test-api /journals/{id}/likes` - 직관일지 좋아요 API 테스트
- `/test-api /journals/{id}/comments` - 직관일지 댓글 API 테스트

## 작업 절차

### 1. 서버 시작
`.env.local` 환경변수를 로드하고 서버 실행:

```bash
export LOCAL_DB_URL='jdbc:mysql://localhost:3306/inningLog?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true'
export LOCAL_DB_USERNAME='root'
export LOCAL_DB_PASSWORD='pw930516'
export JWT_SECRET_KEY='...'
export JWT_EXPIRATION='3600000'
export AWS_REGION='ap-northeast-3'
export LOCAL_AWS_S3_BUCKET='inninglog-1-s3'
export LOCAL_AWS_ACCESS_KEY='...'
export LOCAL_AWS_SECRET_KEY='...'
export LOCAL_ACTIVE='update'
./gradlew bootRun
```

### 2. 서버 상태 확인
```bash
curl -s http://localhost:8080/actuator/health
# 예상: {"status":"UP"}
```

### 3. JWT 토큰 생성
Node.js로 테스트용 JWT 토큰 생성:

```javascript
node -e "
const crypto = require('crypto');
const secret = '{JWT_SECRET_KEY}';
const key = Buffer.from(secret, 'base64');
const header = Buffer.from(JSON.stringify({alg:'HS256',typ:'JWT'})).toString('base64url');
const payload = Buffer.from(JSON.stringify({
  sub:'1',
  iat:Math.floor(Date.now()/1000),
  exp:Math.floor(Date.now()/1000)+3600
})).toString('base64url');
const signature = crypto.createHmac('sha256', key).update(header+'.'+payload).digest('base64url');
console.log(header+'.'+payload+'.'+signature);
"
```

### 4. API 테스트
각 엔드포인트에 대해 테스트 수행:

#### GET 요청
```bash
curl -s -X GET "http://localhost:8080/{endpoint}" \
  -H "Authorization: Bearer {TOKEN}"
```

#### POST 요청
```bash
curl -s -X POST "http://localhost:8080/{endpoint}" \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"key": "value"}'
```

#### DELETE 요청
```bash
curl -s -X DELETE "http://localhost:8080/{endpoint}" \
  -H "Authorization: Bearer {TOKEN}"
```

### 5. 응답 검증
- `code`: "SUCCESS" 확인
- 데이터 필드 값 검증
- 에러 케이스 테스트 (404, 400 등)

## 테스트 시나리오 예시

```bash
# 1. 생성 테스트
echo "=== POST 테스트 ===" && curl -s -X POST ...

# 2. 조회 테스트 (생성된 데이터 확인)
echo "=== GET 테스트 ===" && curl -s -X GET ...

# 3. 삭제 테스트
echo "=== DELETE 테스트 ===" && curl -s -X DELETE ...

# 4. 삭제 후 조회 (데이터 삭제 확인)
echo "=== 삭제 확인 ===" && curl -s -X GET ...
```

## 주요 엔드포인트 참고

| 도메인 | 엔드포인트 패턴 |
|--------|----------------|
| 직관일지 | `/journals/...` |
| 게시글 | `/community/posts/...` |
| 댓글 | `/journals/{id}/comments`, `/community/comments/...` |
| 좋아요 | `/{domain}/{id}/likes` |

## 주의사항
- 테스트 전 DB에 테스트 데이터가 있는지 확인
- memberId=1 사용자가 존재해야 함
- 토큰 만료 시 새로 생성 필요 (1시간)

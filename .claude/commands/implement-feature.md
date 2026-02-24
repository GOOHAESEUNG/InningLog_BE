# TDD 기능 구현

기획서를 기반으로 TDD 방식으로 기능을 구현합니다.

## 사용법
```
/implement-feature <기능명>
```

예시:
- `/implement-feature journal-comment` - 직관일지 댓글 기능 구현
- `/implement-feature journal-like` - 직관일지 좋아요 기능 구현

## 작업 절차

### 1. 기획서 확인
- `docs/plan/feature-plan-{기능명}.md` 파일 읽기
- 체크리스트 확인
- 구현 계획 파악

### 2. RED - 테스트 먼저 작성
- 컨트롤러 테스트 파일 생성 (`src/test/java/.../controller/`)
- MockMvc를 사용한 API 테스트 케이스 작성
- 테스트 실행하여 실패 확인 (엔드포인트 없음)

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class {Feature}ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private {Service} service;

    @Test
    @DisplayName("기능 테스트")
    void testFeature() throws Exception {
        // given, when, then
    }
}
```

### 3. GREEN - 구현
기획서의 구현 계획에 따라 순서대로 구현:

1. **엔티티 수정** - 인터페이스 구현, 필드 추가
2. **ValidateService 수정** - 새 케이스 처리 추가
3. **Controller 생성** - 새 엔드포인트
4. **DTO 수정** - 응답 필드 추가
5. **Usecase/Service 수정** - 비즈니스 로직

### 4. 테스트 통과 확인
```bash
./gradlew test --tests "{테스트클래스명}"
```

### 5. 빌드 확인
```bash
./gradlew build -x test
```

### 6. 기획서 업데이트
- 체크리스트 완료 표시 (`- [ ]` → `- [x]`)
- 상태 변경 (`기획 완료` → `구현 완료`)

## Todo 관리
구현 시 TodoWrite 도구를 사용하여 진행 상황 추적:
- 각 구현 항목을 todo로 등록
- 완료 시 즉시 completed로 변경

## 주의사항
- 반드시 테스트를 먼저 작성 (TDD)
- 기존 아키텍처 패턴 준수
- 기존 코드 스타일과 일관성 유지
- 구현 완료 후 기획서 체크리스트 업데이트

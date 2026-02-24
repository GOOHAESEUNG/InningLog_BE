# Git 커밋 워크플로우

변경사항을 이슈 생성부터 PR 머지까지 자동화합니다.

## 사용법
```
/commit <type> <설명>
```

예시:
- `/commit feat Journal 댓글 API 추가`
- `/commit fix 로그인 버그 수정`
- `/commit docs README 업데이트`

## 작업 절차

### 1. 변경사항 확인
```bash
git status
git diff --stat
```

### 2. 이슈 생성
```bash
gh issue create --title "{type} : {설명}" --body "{변경 내용 요약}"
```
→ 이슈 번호 확인 (예: #93)

### 3. 브랜치 생성
```bash
git checkout -b {type}/#{이슈번호}/{설명}
```
예: `feat/#93/journal-comment`

### 4. 커밋
```bash
git add {파일들}
git commit -m "{type} : {설명} #{이슈번호}"
```
예: `feat : Journal 댓글 API 추가 #93`

### 5. Push & PR 생성
```bash
git push origin {브랜치명}
gh pr create --title "{type} : {설명} #{이슈번호}" --body "Closes #{이슈번호}"
```

### 6. 머지 (선택)
```bash
gh pr merge {PR번호} --merge
```

## Type 종류
- `feat` : 새 기능
- `fix` : 버그 수정
- `chore` : 빌드/설정 변경
- `docs` : 문서
- `style` : 포맷팅
- `refact` : 리팩토링
- `test` : 테스트

## 주의사항
- 이슈 번호를 커밋 메시지와 PR에 포함
- 브랜치명은 `{type}/#{번호}/{설명}` 형식
- PR body에 `Closes #{번호}` 포함하면 머지 시 이슈 자동 종료

# TodoList (Spring Boot)

Spring Boot 기반의 세션 인증 Todo 백엔드 프로젝트입니다.

## 주요 기능
- 회원가입, 로그인, 세션 사용자 조회
- 사용자별 Todo 생성/조회/수정/삭제
- 날짜 기준 조회
- 키워드 검색
- 요청값 검증 및 표준 예외 응답

## 기술 스택
- Java 21
- Spring Boot 3.4.2
- Spring Web
- Spring Data JPA
- MySQL (개발/운영)
- Flyway (DB 마이그레이션)
- H2 (테스트)
- Gradle

## 프로젝트 구조
- `src/main/java/com/example/ToDoList/user`: 사용자 도메인, 인증, 요청/응답 DTO
- `src/main/java/com/example/ToDoList/List`: Todo 도메인, API, 서비스, 검증 DTO
- `src/main/java/com/example/ToDoList/exception`: 전역 예외 처리
- `src/main/resources/db/migration`: Flyway 마이그레이션 SQL
- `src/test`: 테스트 코드 및 테스트 설정

## 실행 전 준비
- JDK 21 설치
- MySQL 실행
- DB `todo_db` 생성
- 프로젝트 루트에 `.env` 파일 생성

예시:
```env
DB_PASSWORD=your_password_here
```

## 실행 방법
```bash
./gradlew bootRun
```
Windows:
```bat
gradlew.bat bootRun
```

## 테스트 실행
```bash
./gradlew test
```
Windows:
```bat
gradlew.bat test
```

테스트는 `src/test/resources/application.yml` 설정에 따라 H2 인메모리 DB를 사용하므로 로컬 MySQL 없이 실행됩니다.

## DB 전략
- 스키마/데이터 변경은 Flyway로 관리
- 메인 환경 JPA는 `ddl-auto: validate` 사용
- `schema.sql`, `data.sql` 자동 초기화는 비활성화

마이그레이션 파일:
- `src/main/resources/db/migration/V1__create_tables.sql`
- `src/main/resources/db/migration/V2__seed_data.sql`

## 인증/세션
- 로그인 성공 시 세션에 `userId`만 저장
- 요청 처리 시 세션의 `userId`로 사용자 식별
- 세션 정보가 없으면 `401` 반환

## API 요약
기본 경로: `/api`

### Users
- `POST /users/register`
- `POST /users/login`
- `GET /users/session`

회원가입 요청 예시:
```json
{
  "userId": "testUser",
  "password": "1234",
  "username": "테스트 유저"
}
```

로그인 요청 예시:
```json
{
  "userId": "testUser",
  "password": "1234"
}
```

### Todos
- `POST /todos/user` (내 전체 Todo)
- `GET /todos/{listId}`
- `POST /todos` (생성)
- `PATCH /todos/{listId}` (완료 여부 변경)
- `PATCH /todos/edit/{listId}` (내용/일정/우선순위 수정)
- `DELETE /todos/{listId}`
- `GET /todos/date?date=YYYY-MM-DD`
- `GET /todos/search?keyword=검색어`

생성/수정 요청 예시:
```json
{
  "content": "공부 하기",
  "priority": 1,
  "startDate": "2025-02-10T10:20:20.000+09:00",
  "endDate": "2025-02-16T20:20:20.000+09:00"
}
```

완료 여부 변경 요청 예시:
```json
{
  "done": true
}
```

## 검증 및 예외 응답
- Bean Validation 적용 (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Max`)
- 전역 예외 핸들러에서 오류 응답 일관 처리
- 오류 응답 형식:
```json
{
  "message": "오류 메시지"
}
```

상태 코드:
- `400`: 잘못된 요청/검증 실패
- `401`: 인증 필요 또는 인증 실패
- `404`: 리소스 없음

## 보안 관련 반영
- `User.password`는 `@JsonIgnore`로 응답에서 제외

## 최근 개선 사항
1. API 응답에서 비밀번호 노출 차단
2. 전역 예외 처리 도입 (`401/404/400` 표준화)
3. 요청 DTO 검증 추가
4. 세션 저장값을 `User` 객체에서 `userId`로 축소
5. Flyway 기반 DB 초기화 전략 일원화
6. 테스트 DB를 H2 인메모리로 전환

# TodoList Frontend (React + Vite)

TodoList 프론트엔드 프로젝트입니다.  
React + Vite 기반이며, 백엔드(Spring Boot)와 세션 기반 인증으로 연동됩니다.

## 기술 스택
- React 18
- Vite 6
- React Router DOM
- react-datepicker
- ESLint

## 필수 환경
- Node.js 18+
- npm

## 시작하기
```bash
npm install
npm run dev
```

기본 개발 서버:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

## 환경변수
`VITE_API_BASE_URL`을 사용해 API 서버 주소를 지정합니다.

`.env` 예시:
```env
VITE_API_BASE_URL=http://localhost:8080
```

동작 방식:
- 값이 있으면: `${VITE_API_BASE_URL}/api` 사용
- 값이 없으면: `/api` 사용 (Vite dev proxy 통해 백엔드로 전달)

설정 파일:
- `src/config.js`
- `vite.config.js`

## 스크립트
- `npm run dev`: 개발 서버 실행
- `npm run build`: 프로덕션 빌드
- `npm run preview`: 빌드 결과 미리보기
- `npm run lint`: 린트 검사

## 백엔드 연동 포인트
- 인증은 세션 쿠키 기반이며 모든 API 요청에 `credentials: "include"`를 사용합니다.
- 주요 API:
  - `POST /api/users/register`
  - `POST /api/users/login`
  - `POST /api/users/logout`
  - `GET /api/users/session`
  - `POST /api/todos/user`
  - `POST /api/todos`
  - `PATCH /api/todos/{listId}`
  - `PATCH /api/todos/edit/{listId}`
  - `DELETE /api/todos/{listId}`
  - `GET /api/todos/date?date=YYYY-MM-DD`
  - `GET /api/todos/search?keyword=...`

## 프로젝트 구조
- `src/pages`: 페이지 컴포넌트 (`Login`, `Register`, `Home`, `NotFound`)
- `src/components`: 공통 UI 및 Todo 컴포넌트
- `src/routes`: 라우터 설정
- `src/config.js`: API base URL 설정

## 최근 반영 사항
- `react-router-dom` 의존성 추가 및 빌드 오류 해결
- 로그아웃 시 서버 세션 종료 요청 추가
- 날짜 조회 파라미터를 로컬 날짜 기준으로 수정
- Vite proxy 기반 API 라우팅으로 정리
- `priority` 타입 숫자 고정
- 한글 깨짐 UI/로그 메시지 복구
- lint/build 통과 상태 유지

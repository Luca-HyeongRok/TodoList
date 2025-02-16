# TodoList
누구나 쉽게 할 수 있는 TodoList를 만들고 싶었습니다.
사용자가 날짜별로 할 일을 추가할 수 있고 수정 및 삭제가 가능한 일정 관리 웹 애플리케이션 입니다.

브랜치에 
front = 프론트
master = 백
입니다.

기능 목록으로는 
로그인 / 로그아웃
할 일 생성 / 수정 / 삭제 / 완료 체크
날짜별 할 일 조회
검색 기능 (할 일 제목으로 검색가능)


주요 페이지
Home.jsx
할 일 목록을 표시하는 메인페이지
useReducer와 useContext를 활용해 상태관리 

주요 컴포넌트 및 설명
-Header.jsx
날짜 선택 / 이동
로그아웃 기능
-Editor.jsx
할 일 추가 GetDataModal.jsx(모달 팝업)과 함께 사용
-List.jsx
현재 날짜 또는 검색된 할 일 목록을 보여주는 리스트
TodoItem.jsx(개별 할 일 아이템)과 함께 사용됨
-TodoItem.jsx
개별 할 일 아이템을 표시하는 컴포넌트
체크박스(완료 여부), 수정 버튼, 삭제 버튼 포함


기능Api

![Api1](https://github.com/Luca-HyeongRok/TodoList/blob/main/Screenshot_1.png)

![Api2](https://github.com/Luca-HyeongRok/TodoList/blob/main/Screenshot_2.png)

![Api3](https://github.com/Luca-HyeongRok/TodoList/blob/main/Screenshot_3.png)

![Api4](https://github.com/Luca-HyeongRok/TodoList/blob/main/Screenshot_4.png)

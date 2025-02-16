import { Link } from "react-router-dom";

const NotFound = () => {
  return (
    <div>
      <h2>404 - 페이지를 찾을 수 없습니다.</h2>
      <Link to="/">홈으로 돌아가기</Link>
    </div>
  );
};

export default NotFound;

import "./Header.css";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { BASE_URL } from "../config";

const Header = ({ selectedDate, setSelectedDate, moveDate }) => {
  const [userName, setUserName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (!storedUser) return;

    try {
      const user = JSON.parse(storedUser);
      if (user?.username) {
        setUserName(user.username);
      }
    } catch (error) {
      console.error("localStorage 파싱 오류:", error);
    }
  }, []);

  const handleLogout = async () => {
    const confirmLogout = window.confirm("로그아웃하시겠습니까?");
    if (!confirmLogout) return;

    try {
      const response = await fetch(`${BASE_URL}/users/logout`, {
        method: "POST",
        credentials: "include",
      });

      if (!response.ok) {
        console.warn("서버 로그아웃 요청 실패:", response.status);
      }
    } catch (error) {
      console.error("로그아웃 요청 중 오류:", error);
    } finally {
      localStorage.removeItem("user");
      alert("로그아웃되었습니다.");
      navigate("/");
    }
  };

  const handleDateChange = (date) => {
    setSelectedDate(date);
    moveDate(0, date);
  };

  return (
    <div className="Header">
      <button onClick={() => moveDate(-1)}>{"<"}</button>
      <div>
        <DatePicker
          selected={selectedDate}
          onChange={handleDateChange}
          dateFormat="yyyy-MM-dd"
          className="date-picker-input"
        />
      </div>
      <button onClick={() => moveDate(1)}>{">"}</button>

      <div className="user-info">
        {userName ? (
          <span>
            {userName}님 |{" "}
            <span className="logout-link" onClick={handleLogout}>
              로그아웃
            </span>
          </span>
        ) : (
          <span>로그인 필요</span>
        )}
      </div>
    </div>
  );
};

export default Header;

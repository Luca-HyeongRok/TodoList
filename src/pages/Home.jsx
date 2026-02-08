import "./Home.css";
import { BASE_URL } from "../config";
import { useState, useReducer, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Editor from "../components/Editor";
import List from "../components/List";
import Footer from "../components/Footer";
import { TodoStateContext, TodoDispatchContext } from "../contexts/TodoContext";

const fetchTodosByDate = async (date) => {
  try {
    const response = await fetch(`${BASE_URL}/todos/date?date=${date}`, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("해당 날짜의 할 일 목록을 불러오지 못했습니다.");
    }

    return await response.json();
  } catch (error) {
    console.error("할 일 목록 불러오기 오류:", error);
    return [];
  }
};

const formatLocalDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const checkSession = async () => {
  try {
    const response = await fetch(`${BASE_URL}/users/session`, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      console.error("세션이 만료되었거나 로그인되지 않았습니다.");
      return null;
    }

    const userData = await response.json();
    localStorage.setItem("user", JSON.stringify(userData));
    return userData;
  } catch (error) {
    console.error("세션 확인 중 오류 발생:", error);
    return null;
  }
};

function reducer(state, action) {
  switch (action.type) {
    case "CREATE":
      return [...state, action.data];
    case "UPDATE":
      return state.map((item) =>
        item.listId === action.targetId
          ? { ...item, done: action.newIsDone }
          : item
      );
    case "DELETE":
      return state.filter((item) => item.listId !== action.targetId);
    case "EDIT":
      return state.map((item) =>
        item.listId === action.targetId
          ? {
              ...item,
              content: action.newContent,
              priority: action.newPriority,
              startDate: action.newStartDate,
              endDate: action.newEndDate,
            }
          : item
      );
    case "SET_TODOS":
      return action.data || [];
    default:
      return state;
  }
}

function Home() {
  const [todos, dispatch] = useReducer(reducer, []);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const navigate = useNavigate();

  const loadTodosByDate = useCallback(async (date) => {
    const formattedDate = formatLocalDate(date);
    const filteredTodos = await fetchTodosByDate(formattedDate);
    dispatch({ type: "SET_TODOS", data: filteredTodos });
  }, []);

  const moveDate = (direction, date = null) => {
    const newDate = date ? new Date(date) : new Date(selectedDate);
    if (!date) {
      newDate.setDate(newDate.getDate() + direction);
    }
    setSelectedDate(newDate);
    loadTodosByDate(newDate);
  };

  const onCreate = useCallback(
    async (content, priority, startDate, endDate) => {
      try {
        const response = await fetch(`${BASE_URL}/todos`, {
          method: "POST",
          body: JSON.stringify({ content, priority, startDate, endDate }),
          headers: { "Content-Type": "application/json" },
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("할 일 추가 실패");
        }

        loadTodosByDate(selectedDate);
      } catch (error) {
        console.error("할 일 추가 중 오류 발생:", error);
      }
    },
    [selectedDate, loadTodosByDate]
  );

  const onChange = useCallback(
    async (listId, newIsDone) => {
      try {
        const response = await fetch(`${BASE_URL}/todos/${listId}`, {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ done: newIsDone }),
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("할 일 상태 변경 실패");
        }

        dispatch({ type: "UPDATE", targetId: listId, newIsDone });
      } catch (error) {
        console.error("할 일 상태 변경 중 오류 발생:", error);
      }
    },
    [dispatch]
  );

  const onEdit = useCallback(
    async (listId, newContent, newPriority, newStartDate, newEndDate) => {
      try {
        const response = await fetch(`${BASE_URL}/todos/edit/${listId}`, {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            content: newContent,
            priority: newPriority,
            startDate: newStartDate,
            endDate: newEndDate,
          }),
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("할 일 수정 실패");
        }

        loadTodosByDate(selectedDate);
      } catch (error) {
        console.error("할 일 수정 중 오류 발생:", error);
      }
    },
    [selectedDate, loadTodosByDate]
  );

  const onDelete = useCallback(
    async (listId) => {
      try {
        const response = await fetch(`${BASE_URL}/todos/${listId}`, {
          method: "DELETE",
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("할 일 삭제 실패");
        }

        loadTodosByDate(selectedDate);
      } catch (error) {
        console.error("할 일 삭제 중 오류 발생:", error);
      }
    },
    [selectedDate, loadTodosByDate]
  );

  useEffect(() => {
    const checkLogin = async () => {
      const user = await checkSession();
      if (!user) {
        navigate("/");
      }
    };
    checkLogin();
  }, [navigate]);

  useEffect(() => {
    loadTodosByDate(selectedDate);
  }, [selectedDate, loadTodosByDate]);

  const memoizedDispatch = useMemo(
    () => ({
      onCreate,
      onChange,
      onDelete,
      onEdit,
    }),
    [onCreate, onChange, onDelete, onEdit]
  );

  return (
    <div className="home-container">
      <Header
        selectedDate={selectedDate}
        setSelectedDate={setSelectedDate}
        moveDate={moveDate}
      />
      <TodoStateContext.Provider value={todos}>
        <TodoDispatchContext.Provider value={memoizedDispatch}>
          <div className="main-content">
            <Editor selectedDate={selectedDate} />
            <List />
          </div>
        </TodoDispatchContext.Provider>
      </TodoStateContext.Provider>
      <Footer />
    </div>
  );
}

export default Home;

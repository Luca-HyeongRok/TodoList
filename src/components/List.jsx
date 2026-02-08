import { useState, useEffect, useMemo, useContext } from "react";
import "./List.css";
import TodoItem from "./TodoItem";
import { TodoStateContext } from "../contexts/TodoContext";
import { BASE_URL } from "../config";

const List = () => {
  const todos = useContext(TodoStateContext);
  const [search, setSearch] = useState("");
  const [filteredTodos, setFilteredTodos] = useState(todos);

  const fetchSearchResults = async (keyword) => {
    try {
      const encodedKeyword = encodeURIComponent(keyword);
      const response = await fetch(
        `${BASE_URL}/todos/search?keyword=${encodedKeyword}`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (!response.ok) {
        throw new Error("검색 결과를 불러오지 못했습니다.");
      }

      return await response.json();
    } catch (error) {
      console.error("검색 오류:", error);
      return [];
    }
  };

  const onChangeSearch = async (e) => {
    const newSearch = e.target.value;
    setSearch(newSearch);

    if (newSearch === "") {
      setFilteredTodos(todos);
    } else {
      const searchResults = await fetchSearchResults(newSearch);
      setFilteredTodos(searchResults);
    }
  };

  useEffect(() => {
    setFilteredTodos(todos);
  }, [todos]);

  const { totalCount, doneCount, notDoneCount } = useMemo(() => {
    const totalCount = todos.length;
    const doneCount = todos.filter((todo) => todo.done).length;
    const notDoneCount = totalCount - doneCount;

    return { totalCount, doneCount, notDoneCount };
  }, [todos]);

  return (
    <div className="List">
      <div className="TotalCheck">
        <div>
          오늘 할 일 전체: {totalCount}개 | 완료: {doneCount}개 | 미완료:{" "}
          {notDoneCount}개
        </div>
      </div>

      <input
        value={search}
        onChange={onChangeSearch}
        placeholder="검색어를 입력하세요"
      />

      <div className="todos_wrapper">
        {filteredTodos.length > 0 ? (
          filteredTodos.map((todo) => <TodoItem key={todo.listId} {...todo} />)
        ) : (
          <p>검색된 할 일이 없습니다.</p>
        )}
      </div>
    </div>
  );
};

export default List;

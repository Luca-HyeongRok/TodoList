import { TodoDispatchContext } from "../pages/Home";
import "./TodoItem.css";
import { memo, useContext, useState } from "react";
import DeleteModal from "./DeleteModal";
import GetDataModal from "./GetDataModal";

const TodoItem = ({ listId, done, content, priority, startDate, endDate }) => {
  const { onChange, onDelete, onEdit } = useContext(TodoDispatchContext);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedTodoId, setSelectedTodoId] = useState(null);

  const onChangeCheckBox = () => {
    if (listId === undefined) {
      console.error("Invalid listId:", listId);
      return;
    }
    onChange(listId, !done);
  };

  const openDeleteModal = (id) => {
    setSelectedTodoId(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
  };

  const onConfirmDelete = () => {
    if (selectedTodoId) {
      onDelete(selectedTodoId);
      closeDeleteModal();
    } else {
      console.error("삭제할 Todo ID가 유효하지 않습니다:", selectedTodoId);
    }
  };

  const openEditModal = () => setIsEditModalOpen(true);
  const closeEditModal = () => setIsEditModalOpen(false);

  const onEditConfirm = (newContent, newPriority, newStartDate, newEndDate) => {
    onEdit(listId, newContent, newPriority, newStartDate, newEndDate);
    setIsEditModalOpen(false);
  };

  const formatDate = (date) => {
    const options = {
      year: "2-digit",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    };
    return new Date(date).toLocaleString("ko-KR", options).replaceAll(". ", ".");
  };

  return (
    <div className="TodoItem">
      <input
        onChange={onChangeCheckBox}
        checked={done}
        type="checkbox"
        data-priority={priority}
      />
      <div className={`content ${done ? "done" : ""}`}>{content}</div>

      <div className="dates">
        시작 : {formatDate(startDate)} <br />
        종료 : {formatDate(endDate)}
      </div>

      <button onClick={() => openDeleteModal(listId)}>삭제</button>
      <button onClick={openEditModal}>수정</button>

      {isDeleteModalOpen && (
        <DeleteModal
          message={`${content} 항목을 삭제하시겠습니까?`}
          onConfirm={onConfirmDelete}
          onCancel={closeDeleteModal}
        />
      )}

      {isEditModalOpen && (
        <GetDataModal
          initialContent={content}
          initialPriority={priority}
          initialStartDate={startDate ? new Date(startDate) : null}
          initialEndDate={endDate ? new Date(endDate) : null}
          onConfirm={onEditConfirm}
          onCancel={closeEditModal}
        />
      )}
    </div>
  );
};

export default memo(TodoItem);

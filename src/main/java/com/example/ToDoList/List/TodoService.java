package com.example.ToDoList.List;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findTodosByUserId(String userId) {
        return todoRepository.findByUser_UserId(userId);
    }

    public Todo getTodoById(Integer listId, String userId) {
        return getOwnedTodo(listId, userId);
    }

    @Transactional
    public Todo updateTodo(Integer listId, String userId, Todo updatedTodo) {
        Todo todo = getOwnedTodo(listId, userId);

        todo.setContent(updatedTodo.getContent());
        todo.setPriority(updatedTodo.getPriority());
        todo.setStartDate(updatedTodo.getStartDate());
        todo.setEndDate(updatedTodo.getEndDate());

        return todoRepository.save(todo);
    }

    @Transactional
    public Todo updateTodoCheckbox(Integer listId, String userId, Todo updatedTodo) {
        Todo todo = getOwnedTodo(listId, userId);
        todo.setDone(updatedTodo.isDone());
        return todoRepository.save(todo);
    }

    @Transactional
    public void deleteTodo(Integer listId, String userId) {
        Todo todo = getOwnedTodo(listId, userId);
        todoRepository.delete(todo);
    }

    public Todo createTodo(Todo newTodo) {
        return todoRepository.save(newTodo);
    }

    public List<Todo> findTodosByDate(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return todoRepository.findByUserIdAndDate(userId, startDate, endDate);
    }

    public List<Todo> searchTodos(String userId, String keyword) {
        return todoRepository.findByUser_UserIdAndContentContainingIgnoreCase(userId, keyword);
    }

    private Todo getOwnedTodo(Integer listId, String userId) {
        Optional<Todo> todoOptional = todoRepository.findByListIdAndUser_UserId(listId, userId);
        if (todoOptional.isEmpty()) {
            throw new RuntimeException("Todo 항목을 찾을 수 없습니다. ID: " + listId);
        }
        return todoOptional.get();
    }
}

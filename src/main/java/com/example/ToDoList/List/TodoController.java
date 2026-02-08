package com.example.ToDoList.List;

import com.example.ToDoList.exception.BadRequestException;
import com.example.ToDoList.exception.UnauthorizedException;
import com.example.ToDoList.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/user")
    public ResponseEntity<List<Todo>> getTodos(HttpServletRequest request) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.findTodosByUserId(user.getUserId()));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<Todo> getTodoByListId(HttpServletRequest request, @PathVariable Integer listId) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.getTodoById(listId, user.getUserId()));
    }

    @PatchMapping("/{listId}")
    public ResponseEntity<Todo> updateTodoCheck(HttpServletRequest request, @PathVariable Integer listId, @RequestBody Todo updatedTodo) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.updateTodoCheckbox(listId, user.getUserId(), updatedTodo));
    }

    @PatchMapping("/edit/{listId}")
    public ResponseEntity<Todo> updateTodo(HttpServletRequest request, @PathVariable Integer listId, @RequestBody Todo updatedTodo) {
        User user = getSessionUserOrThrow(request);
        Todo savedTodo = todoService.updateTodo(listId, user.getUserId(), updatedTodo);
        return ResponseEntity.ok(savedTodo);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteTodo(HttpServletRequest request, @PathVariable Integer listId) {
        User user = getSessionUserOrThrow(request);
        todoService.deleteTodo(listId, user.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(HttpServletRequest request, @RequestBody Todo newTodo) {
        User loggedInUser = getSessionUserOrThrow(request);
        newTodo.setUser(loggedInUser);

        Todo savedTodo = todoService.createTodo(newTodo);
        return ResponseEntity.ok(savedTodo);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Todo>> getTodosByDate(@RequestParam String date, HttpServletRequest request) {
        User user = getSessionUserOrThrow(request);

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            startDateTime = selectedDate.atStartOfDay();
            endDateTime = selectedDate.atTime(23, 59, 59);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        List<Todo> todos = todoService.findTodosByDate(user.getUserId(), startDateTime, endDateTime);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTodos(HttpServletRequest request, @RequestParam(name = "keyword", required = false) String keyword) {
        User user = getSessionUserOrThrow(request);

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Todo> results = todoService.searchTodos(user.getUserId(), keyword);
        return ResponseEntity.ok(results);
    }

    private User getSessionUserOrThrow(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return (User) session.getAttribute("user");
    }
}

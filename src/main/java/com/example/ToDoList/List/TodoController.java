package com.example.ToDoList.List;

import com.example.ToDoList.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getTodos(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        return ResponseEntity.ok(todoService.findTodosByUserId(user.getUserId()));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<?> getTodoByListId(HttpServletRequest request, @PathVariable Integer listId) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        try {
            return ResponseEntity.ok(todoService.getTodoById(listId, user.getUserId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{listId}")
    public ResponseEntity<?> updateTodoCheck(HttpServletRequest request, @PathVariable Integer listId, @RequestBody Todo updatedTodo) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        try {
            return ResponseEntity.ok(todoService.updateTodoCheckbox(listId, user.getUserId(), updatedTodo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/{listId}")
    public ResponseEntity<?> updateTodo(HttpServletRequest request, @PathVariable Integer listId, @RequestBody Todo updatedTodo) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        try {
            Todo savedTodo = todoService.updateTodo(listId, user.getUserId(), updatedTodo);
            return ResponseEntity.ok(savedTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<?> deleteTodo(HttpServletRequest request, @PathVariable Integer listId) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        try {
            todoService.deleteTodo(listId, user.getUserId());
            return ResponseEntity.ok("삭제 완료");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createTodo(HttpServletRequest request, @RequestBody Todo newTodo) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User loggedInUser = (User) session.getAttribute("user");
        newTodo.setUser(loggedInUser);

        Todo savedTodo = todoService.createTodo(newTodo);
        return ResponseEntity.ok(savedTodo);
    }

    @GetMapping("/date")
    public ResponseEntity<?> getTodosByDate(@RequestParam String date, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            startDateTime = selectedDate.atStartOfDay();
            endDateTime = selectedDate.atTime(23, 59, 59);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        List<Todo> todos = todoService.findTodosByDate(user.getUserId(), startDateTime, endDateTime);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTodos(HttpServletRequest request, @RequestParam(name = "keyword", required = false) String keyword) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        User user = (User) session.getAttribute("user");
        List<Todo> results = todoService.searchTodos(user.getUserId(), keyword);
        return ResponseEntity.ok(results);
    }
}

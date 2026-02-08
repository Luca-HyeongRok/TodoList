package com.example.ToDoList.List;

import com.example.ToDoList.exception.BadRequestException;
import com.example.ToDoList.exception.UnauthorizedException;
import com.example.ToDoList.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Validated
public class TodoController {

    private static final String SESSION_USER_ID = "userId";

    private final TodoService todoService;
    private final UserService userService;

    @Autowired
    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<List<Todo>> getTodos(HttpServletRequest request) {
        String userId = getSessionUserIdOrThrow(request);
        return ResponseEntity.ok(todoService.findTodosByUserId(userId));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<Todo> getTodoByListId(HttpServletRequest request, @PathVariable Integer listId) {
        String userId = getSessionUserIdOrThrow(request);
        return ResponseEntity.ok(todoService.getTodoById(listId, userId));
    }

    @PatchMapping("/{listId}")
    public ResponseEntity<Todo> updateTodoCheck(HttpServletRequest request, @PathVariable Integer listId,
                                                @Valid @RequestBody TodoCheckUpdateRequest updatedTodo) {
        String userId = getSessionUserIdOrThrow(request);
        return ResponseEntity.ok(todoService.updateTodoCheckbox(listId, userId, updatedTodo));
    }

    @PatchMapping("/edit/{listId}")
    public ResponseEntity<Todo> updateTodo(HttpServletRequest request, @PathVariable Integer listId,
                                           @Valid @RequestBody TodoUpsertRequest updatedTodo) {
        String userId = getSessionUserIdOrThrow(request);
        return ResponseEntity.ok(todoService.updateTodo(listId, userId, updatedTodo));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteTodo(HttpServletRequest request, @PathVariable Integer listId) {
        String userId = getSessionUserIdOrThrow(request);
        todoService.deleteTodo(listId, userId);
        return ResponseEntity.ok("삭제 완료");
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(HttpServletRequest request, @Valid @RequestBody TodoUpsertRequest newTodo) {
        String userId = getSessionUserIdOrThrow(request);
        return ResponseEntity.ok(todoService.createTodo(userService.getUserOrThrow(userId), newTodo));
    }

    @GetMapping("/date")
    public ResponseEntity<List<Todo>> getTodosByDate(@RequestParam String date, HttpServletRequest request) {
        String userId = getSessionUserIdOrThrow(request);

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            LocalDate selectedDate = LocalDate.parse(date);
            startDateTime = selectedDate.atStartOfDay();
            endDateTime = selectedDate.atTime(23, 59, 59);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        return ResponseEntity.ok(todoService.findTodosByDate(userId, startDateTime, endDateTime));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Todo>> searchTodos(
            HttpServletRequest request,
            @RequestParam(name = "keyword") @NotBlank(message = "검색어는 필수입니다.") String keyword
    ) {
        String userId = getSessionUserIdOrThrow(request);
        return ResponseEntity.ok(todoService.searchTodos(userId, keyword));
    }

    private String getSessionUserIdOrThrow(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return (String) session.getAttribute(SESSION_USER_ID);
    }
}

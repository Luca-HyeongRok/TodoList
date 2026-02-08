package com.example.ToDoList.List;

import com.example.ToDoList.exception.BadRequestException;
import com.example.ToDoList.exception.UnauthorizedException;
import com.example.ToDoList.user.User;
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
    public ResponseEntity<Todo> updateTodoCheck(HttpServletRequest request, @PathVariable Integer listId,
                                                @Valid @RequestBody TodoCheckUpdateRequest updatedTodo) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.updateTodoCheckbox(listId, user.getUserId(), updatedTodo));
    }

    @PatchMapping("/edit/{listId}")
    public ResponseEntity<Todo> updateTodo(HttpServletRequest request, @PathVariable Integer listId,
                                           @Valid @RequestBody TodoUpsertRequest updatedTodo) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.updateTodo(listId, user.getUserId(), updatedTodo));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteTodo(HttpServletRequest request, @PathVariable Integer listId) {
        User user = getSessionUserOrThrow(request);
        todoService.deleteTodo(listId, user.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(HttpServletRequest request, @Valid @RequestBody TodoUpsertRequest newTodo) {
        User loggedInUser = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.createTodo(loggedInUser, newTodo));
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

        return ResponseEntity.ok(todoService.findTodosByDate(user.getUserId(), startDateTime, endDateTime));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Todo>> searchTodos(
            HttpServletRequest request,
            @RequestParam(name = "keyword") @NotBlank(message = "검색어는 필수입니다.") String keyword
    ) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(todoService.searchTodos(user.getUserId(), keyword));
    }

    private User getSessionUserOrThrow(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return (User) session.getAttribute("user");
    }
}

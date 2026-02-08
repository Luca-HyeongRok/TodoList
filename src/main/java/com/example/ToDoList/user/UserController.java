package com.example.ToDoList.user;

import com.example.ToDoList.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest userDto) {
        String response = userService.registerUser(userDto);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(HttpServletRequest request, @Valid @RequestBody LoginRequest userDTO) {
        User user = userService.authenticate(userDTO.getUserId(), userDTO.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        return ResponseEntity.ok(new UserDTO(user.getUserId(), null, user.getUsername()));
    }

    @GetMapping("/session")
    public ResponseEntity<UserDTO> getSessionUser(HttpServletRequest request) {
        User user = getSessionUserOrThrow(request);
        return ResponseEntity.ok(new UserDTO(user.getUserId(), null, user.getUsername()));
    }

    private User getSessionUserOrThrow(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return (User) session.getAttribute("user");
    }
}

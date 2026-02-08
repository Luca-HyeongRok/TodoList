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

    private static final String SESSION_USER_ID = "userId";

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
        session.setAttribute(SESSION_USER_ID, user.getUserId());

        return ResponseEntity.ok(new UserDTO(user.getUserId(), null, user.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));
    }

    @GetMapping("/session")
    public ResponseEntity<UserDTO> getSessionUser(HttpServletRequest request) {
        String userId = getSessionUserIdOrThrow(request);
        User user = userService.getUserOrThrow(userId);
        return ResponseEntity.ok(new UserDTO(user.getUserId(), null, user.getUsername()));
    }

    private String getSessionUserIdOrThrow(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return (String) session.getAttribute(SESSION_USER_ID);
    }
}

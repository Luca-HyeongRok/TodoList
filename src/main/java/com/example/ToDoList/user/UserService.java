package com.example.ToDoList.user;

import com.example.ToDoList.exception.BadRequestException;
import com.example.ToDoList.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public String registerUser(RegisterRequest userDto) {
        Optional<User> existingUser = userRepository.findByUserId(userDto.getUserId());

        if (existingUser.isPresent()) {
            throw new BadRequestException("이미 존재하는 아이디입니다.");
        }

        User newUser = new User(
                userDto.getUserId(),
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword())
        );

        userRepository.save(newUser);
        return "회원가입 성공";
    }

    public User authenticate(String userId, String rawPassword) {
        User user = getUserOrThrow(userId);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    public User getUserOrThrow(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));
    }
}

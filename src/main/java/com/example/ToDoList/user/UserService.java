package com.example.ToDoList.user;

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
    public String registerUser(UserDTO userDto) {
        Optional<User> existingUser = userRepository.findByUserId(userDto.getUserId());

        if (existingUser.isPresent()) {
            return "이미 존재하는 아이디입니다.";
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
        Optional<User> existingUser = userRepository.findByUserId(userId);
        if (existingUser.isEmpty()) {
            return null;
        }

        User user = existingUser.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }

        return user;
    }
}

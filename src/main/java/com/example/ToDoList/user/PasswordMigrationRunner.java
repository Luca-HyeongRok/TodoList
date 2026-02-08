package com.example.ToDoList.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordMigrationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        boolean updated = false;

        for (User user : users) {
            String password = user.getPassword();
            if (password == null || isBcryptHash(password)) {
                continue;
            }

            user.setPassword(passwordEncoder.encode(password));
            updated = true;
        }

        if (updated) {
            userRepository.saveAll(users);
        }
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}

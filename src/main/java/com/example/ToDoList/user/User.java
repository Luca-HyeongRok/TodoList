package com.example.ToDoList.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(nullable = false, unique = true, length = 191)
    private String userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;


    @Builder
    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
}

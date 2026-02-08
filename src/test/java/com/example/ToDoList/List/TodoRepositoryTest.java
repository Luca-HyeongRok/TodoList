package com.example.ToDoList.List;

import com.example.ToDoList.user.User;
import com.example.ToDoList.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // 테스트가 끝난 후 DB 상태를 롤백
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Todo testTodo1;
    private Todo testTodo2;

    // ?뚯뒪???곗씠?곕? 誘몃━ 以鍮?
    @BeforeEach
    public void setUp() {
        // ?좎? ?곗씠??以鍮?
        testUser = new User();
        testUser.setUserId("testUser");
        testUser.setUsername("Test User");
        testUser.setPassword("password");
        userRepository.save(testUser);

        testTodo1 = new Todo();
        testTodo1.setUser(testUser);
        testTodo1.setContent("Test Todo 1");
        testTodo1.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        testTodo1.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        todoRepository.save(testTodo1);

        testTodo2 = new Todo();
        testTodo2.setUser(testUser);
        testTodo2.setContent("Test Todo 2");
        testTodo2.setStartDate(Timestamp.valueOf(LocalDateTime.now().minusDays(2)));
        testTodo2.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
        todoRepository.save(testTodo2);
    }

    @Test
    public void findByUser_UserId_Test() {
        List<Todo> todos = todoRepository.findByUser_UserId("testUser");

        assertNotNull(todos);
        assertEquals(2, todos.size(), "testUser??Todo媛 2媛쒖뿬???⑸땲??");

        // testTodo1怨?testTodo2???댁슜 ?뺤씤
        assertTrue(todos.stream().anyMatch(todo -> todo.getContent().equals("Test Todo 1")));
        assertTrue(todos.stream().anyMatch(todo -> todo.getContent().equals("Test Todo 2")));
    }

    @Test
    public void findByUserIdAndDate_Test() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);

        List<Todo> todos = todoRepository.findByUserIdAndDate("testUser", startDate, endDate);

        assertNotNull(todos);
        assertEquals(2, todos.size(), "testUser??湲곌컙 ??Todo媛 2媛쒖뿬???⑸땲??");
    }

    @Test
    public void findByContentContainingIgnoreCase_Test() {
        List<Todo> todos = todoRepository.findByUser_UserIdAndContentContainingIgnoreCase("testUser", "Test Todo");

        assertNotNull(todos);
        assertEquals(2, todos.size(), "Test Todo瑜??ы븿?섎뒗 Todo媛 2媛쒖뿬???⑸땲??");
    }
}

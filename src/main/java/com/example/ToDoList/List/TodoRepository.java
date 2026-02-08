package com.example.ToDoList.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    @Query("SELECT t FROM Todo t WHERE t.user.userId = :userId")
    List<Todo> findByUser_UserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM LIST_TB t WHERE t.user_id = :userId AND t.start_date <= :endDate AND t.end_date >= :startDate",
            nativeQuery = true)
    List<Todo> findByUserIdAndDate(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Todo> findByUser_UserIdAndContentContainingIgnoreCase(String userId, String keyword);

    Optional<Todo> findByListIdAndUser_UserId(Integer listId, String userId);
}

package com.canhtv05.user.repository;

import com.canhtv05.user.enity.User;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);

    @Query("""
            SELECT u
            FROM User u
            WHERE (:email IS NULL OR LOWER(u.email) = LOWER(CONCAT('%', :email, '%')))
            AND (:username IS NULL OR LOWER(u.username) = LOWER(CONCAT('%', :username, '%')) )
            """)
    Page<User> getAllUsers(@Param("email") String email,
                           @Param("username") String username,
                           Pageable pageable);
}

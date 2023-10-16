package com.practice.repository;

import com.practice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM user_details WHERE email =:email", nativeQuery = true)
    User getUserByUserName(@Param("email") String userName);
}

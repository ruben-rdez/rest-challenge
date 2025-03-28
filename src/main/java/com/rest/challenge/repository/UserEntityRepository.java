package com.rest.challenge.repository;

import com.rest.challenge.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.name = ?1")
    List<UserEntity> findByName(String name);

    @Query(value = "SELECT * FROM user WHERE email LIKE %:mail%", nativeQuery = true)
    UserEntity findByEmail(@Param("mail") String mail);
}

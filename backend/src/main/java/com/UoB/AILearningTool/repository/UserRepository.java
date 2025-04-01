package com.UoB.AILearningTool.repository;

import com.UoB.AILearningTool.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Query("SELECT c FROM UserEntity c WHERE c.sessionID = :sessionID")
    Optional<UserEntity> findBySessionID(String sessionID);

    Optional<UserEntity> findByUsername(String username); 
}

package com.UoB.AILearningTool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.UoB.AILearningTool.model.UserEntity;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username); 
}

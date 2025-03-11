package com.UoB.AILearningTool.repository;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, String> {


    @Query("SELECT c FROM ChatEntity c WHERE c.sessionID = :sessionID")
    Optional<ChatEntity> findBySessionID(@Param("sessionID") String sessionID);

    List<ChatEntity> findByOwner(UserEntity owner);
}

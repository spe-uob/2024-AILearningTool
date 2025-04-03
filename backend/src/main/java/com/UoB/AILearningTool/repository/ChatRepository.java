package com.UoB.AILearningTool.repository;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, String> {


//    @Query("SELECT c FROM ChatEntity c WHERE c.threadID = :threadID")
//    Optional<ChatEntity> findBySessionID(@Param("threadID") String sessionID);

    List<ChatEntity> findByOwner(UserEntity owner);
}

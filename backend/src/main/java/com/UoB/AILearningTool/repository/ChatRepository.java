package com.UoB.AILearningTool.repository;

import com.UoB.AILearningTool.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, String> {
}

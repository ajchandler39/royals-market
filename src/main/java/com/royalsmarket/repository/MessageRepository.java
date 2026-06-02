package com.royalsmarket.repository;

import com.royalsmarket.entity.Conversation;
import com.royalsmarket.entity.Message;
import com.royalsmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);

    /** Count unread messages across all of a user's conversations (not sent by them). */
    @Query("""
            select count(m) from Message m
            where m.readAt is null
              and m.sender <> :user
              and (m.conversation.participantA = :user or m.conversation.participantB = :user)
            """)
    long countUnreadForUser(@Param("user") User user);
}

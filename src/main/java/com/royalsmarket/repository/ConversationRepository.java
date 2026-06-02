package com.royalsmarket.repository;

import com.royalsmarket.entity.Conversation;
import com.royalsmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
            select c from Conversation c
            where c.participantA = :user or c.participantB = :user
            order by c.lastMessageAt desc
            """)
    List<Conversation> findForUser(@Param("user") User user);

    @Query("""
            select c from Conversation c
            where c.listing.id = :listingId
              and ((c.participantA = :a and c.participantB = :b)
                or (c.participantA = :b and c.participantB = :a))
            """)
    Optional<Conversation> findByListingAndParticipants(
            @Param("listingId") Long listingId,
            @Param("a") User a,
            @Param("b") User b);
}

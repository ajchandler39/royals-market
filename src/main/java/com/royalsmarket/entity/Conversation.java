package com.royalsmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Getter
@Setter
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The listing this conversation is about (nullable for general chats). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_a")
    private User participantA;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_b")
    private User participantB;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastMessageAt = LocalDateTime.now();

    /** The participant who is not the given user. */
    @Transient
    public User other(User me) {
        return participantA.getId().equals(me.getId()) ? participantB : participantA;
    }

    @Transient
    public boolean involves(User user) {
        return participantA.getId().equals(user.getId()) || participantB.getId().equals(user.getId());
    }
}

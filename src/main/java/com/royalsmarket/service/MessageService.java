package com.royalsmarket.service;

import com.royalsmarket.entity.Conversation;
import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.Message;
import com.royalsmarket.entity.User;
import com.royalsmarket.repository.ConversationRepository;
import com.royalsmarket.repository.ListingRepository;
import com.royalsmarket.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;

    public List<Conversation> inbox(User user) {
        return conversationRepository.findForUser(user);
    }

    public long unreadCount(User user) {
        return messageRepository.countUnreadForUser(user);
    }

    public Conversation getForUser(Long conversationId, User user) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new DomainException("Conversation not found."));
        if (!c.involves(user)) {
            throw new DomainException("You're not part of this conversation.");
        }
        return c;
    }

    /** Messages in a conversation; also marks the other party's messages as read. */
    @Transactional
    public List<Message> openConversation(Conversation c, User reader) {
        List<Message> messages = messageRepository.findByConversationOrderBySentAtAsc(c);
        for (Message m : messages) {
            if (m.getReadAt() == null && !m.getSender().getId().equals(reader.getId())) {
                m.setReadAt(LocalDateTime.now());
            }
        }
        return messages;
    }

    /** Start (or reuse) a conversation with the seller of a listing, then send the first message. */
    @Transactional
    public Conversation contactSeller(Long listingId, User buyer, String body) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new DomainException("Listing not found."));
        User seller = listing.getSeller();
        if (seller.getId().equals(buyer.getId())) {
            throw new DomainException("You can't message yourself.");
        }
        Conversation c = conversationRepository
                .findByListingAndParticipants(listingId, buyer, seller)
                .orElseGet(() -> {
                    Conversation nc = new Conversation();
                    nc.setListing(listing);
                    nc.setParticipantA(buyer);
                    nc.setParticipantB(seller);
                    return conversationRepository.save(nc);
                });
        send(c, buyer, body);
        return c;
    }

    @Transactional
    public Message send(Conversation c, User sender, String body) {
        if (!c.involves(sender)) {
            throw new DomainException("You're not part of this conversation.");
        }
        Message m = new Message();
        m.setConversation(c);
        m.setSender(sender);
        m.setBody(body.trim());
        m = messageRepository.save(m);
        c.setLastMessageAt(m.getSentAt());
        conversationRepository.save(c);
        return m;
    }
}

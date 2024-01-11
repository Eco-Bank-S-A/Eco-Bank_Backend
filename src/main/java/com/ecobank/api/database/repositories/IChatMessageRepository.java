package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.ChatMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface IChatMessageRepository extends CrudRepository<ChatMessage, Long> {
    ArrayList<ChatMessage> findByChatId(long chatId);
}

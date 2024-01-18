package com.ecobank.api.controllers;

import com.ecobank.api.database.entities.Chat;
import com.ecobank.api.database.entities.ChatMessage;
import com.ecobank.api.database.repositories.IChatMessageRepository;
import com.ecobank.api.database.repositories.IChatRepository;
import com.ecobank.api.models.chat.AddChatRequest;
import com.ecobank.api.models.chat.AddMessageRequest;
import com.ecobank.api.models.chat.ChatDto;
import com.ecobank.api.models.chat.ChatMessageDto;
import com.ecobank.api.services.ChatBroker;
import com.ecobank.api.services.abstractions.IUserService;
import org.apache.logging.log4j.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private IChatRepository chatRepository;
    private IChatMessageRepository chatMessageRepository;
    private IUserService userService;
    private ChatBroker broker;

    public ChatController(IChatRepository chatRepository, IChatMessageRepository chatMessageRepository, IUserService userService, ChatBroker broker) {
        this.chatRepository = chatRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
        this.broker = broker;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<ChatDto>> getChats() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.getUserByEmail(userEmail).get();

        var chats = chatRepository.findByUser1(user.getId());
        var chatsDto = new ArrayList<ChatDto>();

        for (Chat chat : chats) {
            var extUser = chat.getUser1().getId().equals(user.getId()) ? chat.getUser2() : chat.getUser1();
            var chatDto = ChatDto
                    .builder()
                    .id(chat.getId())
                    .userName(extUser.getFirstName() + " " + extUser.getLastName())
                    .email(extUser.getEmail())
                    .build();

            chatsDto.add(chatDto);
        }


        return ResponseEntity.ok(chatsDto);
    }

    @PostMapping("/add")
    public ResponseEntity addChat(@RequestBody AddChatRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.getUserByEmail(userEmail).get();

        var extUser = userService.getUserByEmail(request.getEmail());
        if (extUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var chat = chatRepository.findByUser1AndUser2(user.getId(), extUser.get().getId());
        if(chat.isPresent()) {
            return ResponseEntity.ok().build();
        }

        var newChat = Chat
                .builder()
                .user1(user)
                .user2(extUser.get())
                .build();

        chatRepository.save(newChat);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/message/{chatId}")
    public ResponseEntity<ArrayList<ChatMessageDto>> getMessages(@PathVariable long chatId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var messages = chatMessageRepository.findByChatId(chatId);
        var messagesDto = new ArrayList<ChatMessageDto>();

        for (ChatMessage message : messages) {
            var messageDto = ChatMessageDto
                    .builder()
                    .id(message.getId())
                    .chatId(message.getChat().getId())
                    .sender(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                    .message(message.getMessage())
                    .isMine(Objects.equals(message.getSender().getEmail(), userEmail))
                    .build();

            messagesDto.add(messageDto);
        }

        return ResponseEntity.ok(messagesDto);
    }

    @PostMapping("/message")
    public ResponseEntity addMessage(@RequestBody AddMessageRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.getUserByEmail(userEmail).get();
        var chat = chatRepository.findById(request.getChatId());

        var newMessage = ChatMessage
                .builder()
                .chat(chat.get())
                .sender(user)
                .message(request.getMessage())
                .build();

        newMessage = chatMessageRepository.save(newMessage);

        broker.publish(userEmail, ChatMessageDto.builder()
                        .id(newMessage.getId())
                        .chatId(newMessage.getChat().getId())
                        .message(newMessage.getMessage())
                        .sender(newMessage.getSender().getFirstName() + " " + newMessage.getSender().getLastName())
                        .isMine(false)
                .build());

        return  ResponseEntity.ok().build();
    }
}

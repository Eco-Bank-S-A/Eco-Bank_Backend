package com.ecobank.api.models.chat;

import lombok.Data;

@Data
public class AddMessageRequest {
    private String message;
    private long chatId;
}

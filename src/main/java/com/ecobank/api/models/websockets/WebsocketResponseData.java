package com.ecobank.api.models.websockets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsocketResponseData<T> {
    private String command;
    private T data;
}

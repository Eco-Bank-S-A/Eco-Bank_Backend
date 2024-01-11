package com.ecobank.api.models.transfer;

import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.models.user.UserInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferInfo {
    public TransferInfo(Transaction transfer) {
        this.senderAccount = new UserInfo(transfer.getSender());
        this.receiverAccount = new UserInfo(transfer.getReceiver());
        this.amount = transfer.getBalance();
        this.status = transfer.getStatus();
        this.uuid = transfer.getUuid();
        this.creationDate = transfer.getCreationDate();
    }
    private UserInfo receiverAccount;
    private UserInfo senderAccount;
    private BigDecimal amount;
    private int status;
    private String uuid;
    private LocalDateTime creationDate;
}

package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.User;
import com.ecobank.api.models.transfer.TransferInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ITransferService {
    boolean transferMoney(String userEmail, String recipientIBAN, String title, BigDecimal amount);
    int countTransferHistory(String userEmail, int[] operationsTypes, LocalDateTime from, LocalDateTime to);

    List<TransferInfo> getTransferHistory(String userEmail, int[] operationsTypes, LocalDateTime from, LocalDateTime to, int batchSize, int batchNumber);
}

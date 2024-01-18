package com.ecobank.api.services.abstractions;

import com.ecobank.api.services.subscribers.Co2Subscriber;
import org.springframework.scheduling.annotation.Scheduled;

public interface ICo2StockRateService {
    @Scheduled(fixedRate = 5000)
    void co2StockRatePuller();

    void subscribe(Co2Subscriber subscriber);

    void unsubscribe(String connectionId);
}

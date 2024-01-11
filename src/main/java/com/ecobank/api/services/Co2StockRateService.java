package com.ecobank.api.services;

import com.ecobank.api.database.entities.Co2Stock;
import com.ecobank.api.database.repositories.ICo2StockRepository;
import com.ecobank.api.models.co2.Co2StockPriceResponse;
import com.ecobank.api.services.abstractions.co2.ICo2Subscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class Co2StockRateService {

    @Value("${application.co2.api}")
    private String uri;

    private ArrayList<ICo2Subscriber> subscribers = new ArrayList<>();

    ICo2StockRepository repository;
    public Co2StockRateService(ICo2StockRepository co2StockRepository) {
        repository = co2StockRepository;
    }

    @Scheduled(fixedRate = 5000*100000)
    public void co2StockRatePuller() {
        Co2StockPriceResponse result;
        try {
            RestTemplate restTemplate = new RestTemplate();
            result = restTemplate.getForObject(uri, Co2StockPriceResponse.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }


        System.out.println(result.getValue());
        var stock = Co2Stock
            .builder()
            .co2StockRate(result.getValue())
            .createdAt(LocalDateTime.now(ZoneOffset.UTC))
            .build();

        repository.save(stock);

        for (var subscriber : subscribers) {
            subscriber.notify(result.getValue());
        }
    }

    public void subscribe(ICo2Subscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            return;
        }
        subscribers.add(subscriber);
    }

    public void unsubscribe(ICo2Subscriber subscriber) {
        subscribers.remove(subscriber);
    }
}

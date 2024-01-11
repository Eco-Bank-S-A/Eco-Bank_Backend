package com.ecobank.api.services;

import com.ecobank.api.database.entities.Co2Stock;
import com.ecobank.api.database.repositories.ICo2StockRepository;
import com.ecobank.api.models.co2.Co2StockPriceResponse;
import com.ecobank.api.services.abstractions.co2.ICo2Subscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

@Service
public class Co2StockRateService {

    @Value("${application.co2.api}")
    private String uri;

    private ArrayList<ICo2Subscriber> subscribers = new ArrayList<>();

    ICo2StockRepository repository;
    public Co2StockRateService(ICo2StockRepository co2StockRepository) {
        repository = co2StockRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void co2StockRatePuller() {
        Co2StockPriceResponse result;
        try {
            RestTemplate restTemplate = new RestTemplate();
            result = restTemplate.getForObject(uri, Co2StockPriceResponse.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        var rand = new Random();
        var sellStock = BigDecimal.valueOf(result.getValue() * rand.nextDouble(0.9, 0.99)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        var stock = Co2Stock
            .builder()
            .co2BuyStock(result.getValue())
            .co2SellStock(sellStock)
            .createdAt(LocalDateTime.now(ZoneOffset.UTC))
            .build();

        stock = repository.save(stock);

        for (var subscriber : subscribers) {
            subscriber.notify(stock.getId(), stock.getCo2BuyStock(), stock.getCo2SellStock());
        }

        System.out.println("Buy: " + stock.getCo2BuyStock() + " Sell: " + stock.getCo2SellStock());
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

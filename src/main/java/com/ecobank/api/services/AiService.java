package com.ecobank.api.services;

import com.ecobank.api.database.entities.Co2Stock;
import com.ecobank.api.models.ai.AiFraudResponse;
import com.ecobank.api.models.ai.AiCreditScoreResponse;
import com.ecobank.api.models.co2.Co2StockPriceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.ecobank.api.database.entities.Account;

@Service
public class AiService {

    @Value("${application.ai.api}")
    private String uri;

    @Value("${application.credit.api}")
    private String creditUri;

    public boolean predictFraud(BigDecimal balance, double creditScore, BigDecimal amount) { //predictTransaction
        AiFraudResponse result;
        //System.out.println(creditScore);
        try {
            // Nagłówki http
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Json
            String jsonBody = "{ \"Balance\": \"" + balance + "\", \"CreditScore\": \"" + creditScore + "\", \"TransactionValue\": \"" + amount + "\" }";
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            result = restTemplate.postForObject(uri + "predictTransactions", requestEntity, AiFraudResponse.class);

            System.out.print("isFraud: ");
            System.out.println(result.getFraud());

            return (result.getFraud() != 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int getCreditScore(Account account) {
        AiCreditScoreResponse result;

        try {
            RestTemplate restTemplate = new RestTemplate();
            result = restTemplate.getForObject(creditUri, AiCreditScoreResponse.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 50;
        }

        return result.getCreditScore();
    }
}

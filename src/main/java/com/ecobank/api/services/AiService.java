package com.ecobank.api.services;

import com.ecobank.api.models.ai.AiFraudResponse;
import com.ecobank.api.services.abstractions.IAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

@Service
public class AiService implements IAiService {

    @Value("${application.ai.api}")
    private String uri;
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
}

package com.ecobank.api.services.abstractions.co2;

public interface ICo2Subscriber {

    String getSessionId();
    void notifyCo2(long id, double co2BuyStock, double co2SellStock);

}

package com.ecobank.api.services.abstractions.co2;

public interface ICo2Subscriber {
    public void notify(long id, double co2BuyStock, double co2SellStock);

}

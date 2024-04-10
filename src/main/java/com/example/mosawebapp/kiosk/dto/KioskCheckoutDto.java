package com.example.mosawebapp.kiosk.dto;

import java.util.List;

public class KioskCheckoutDto {
  private float priceToPay;
  private String queueingNumber;
  private List<KioskDto> kiosks;

  public KioskCheckoutDto(){}

  public KioskCheckoutDto(List<KioskDto> kiosks) {
    this.priceToPay = computePriceToPay(kiosks);
    this.kiosks = kiosks;
  }

  private float computePriceToPay(List<KioskDto> kiosks){
    float price = 0;

    for(KioskDto kiosk: kiosks){
      price += kiosk.getTotalPrice();
    }

    return price;
  }

  public float getPriceToPay() {
    return priceToPay;
  }

  public void setPriceToPay(float priceToPay) {
    this.priceToPay = priceToPay;
  }

  public List<KioskDto> getKiosks() {
    return kiosks;
  }

  public void setKiosks(List<KioskDto> kiosks) {
    this.kiosks = kiosks;
  }

  public String getQueueingNumber() {
    return queueingNumber;
  }

  public void setQueueingNumber(String queueingNumber) {
    this.queueingNumber = queueingNumber;
  }
}

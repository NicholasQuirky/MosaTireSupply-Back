package com.example.mosawebapp.onsite_order.dto;

import java.util.List;

public class OnsiteOrderCheckoutDto {
  private float priceToPay;
  private List<OnsiteOrderDto> orders;

  public OnsiteOrderCheckoutDto(){}
  public OnsiteOrderCheckoutDto(List<OnsiteOrderDto> orders){
    this.priceToPay = computePriceToPay(orders);
    this.orders = orders;
  }

  private float computePriceToPay(List<OnsiteOrderDto> orders){
    float price = 0;

    for(OnsiteOrderDto dto: orders){
      price += dto.getTotalPrice();
    }

    return price;
  }

  public float getPriceToPay() {
    return priceToPay;
  }

  public void setPriceToPay(float priceToPay) {
    this.priceToPay = priceToPay;
  }

  public List<OnsiteOrderDto> getOrders() {
    return orders;
  }

  public void setOrders(List<OnsiteOrderDto> orders) {
    this.orders = orders;
  }
}

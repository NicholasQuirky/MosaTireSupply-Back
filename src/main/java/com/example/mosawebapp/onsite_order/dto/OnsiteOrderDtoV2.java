package com.example.mosawebapp.onsite_order.dto;

import com.example.mosawebapp.cart.dto.CartDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class OnsiteOrderDtoV2 {
  private float totalCartPrice;
  private List<OnsiteOrderDto> orders;

  public OnsiteOrderDtoV2(){}
  public OnsiteOrderDtoV2(List<OnsiteOrderDto> orders){
    this.totalCartPrice = computePriceToPay(orders);
    this.orders = orders;
  }

  private float computePriceToPay(List<OnsiteOrderDto> orders){
    float price = 0;

    for(OnsiteOrderDto dto: orders){
      price += dto.getTotalPrice();
    }

    return price / 2;
  }

  public float getTotalCartPrice() {
    return totalCartPrice;
  }

  public void setTotalCartPrice(float totalCartPrice) {
    this.totalCartPrice = totalCartPrice;
  }

  public List<OnsiteOrderDto> getOrders() {
    return orders;
  }

  public void setOrders(List<OnsiteOrderDto> orders) {
    this.orders = orders;
  }
}

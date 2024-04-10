package com.example.mosawebapp.cart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class CartDtoV2 {
  private float totalCartPrice;
  private List<CartDto> cartOrders;

  public CartDtoV2(){}
  public CartDtoV2(List<CartDto> cartOrders) {
    this.totalCartPrice = computePriceToPay(cartOrders);
    this.cartOrders = cartOrders;
  }

  private float computePriceToPay(List<CartDto> cartOrders){
    float price = 0;

    for(CartDto dto: cartOrders){
      price += dto.getTotalPrice();
    }

    return price / 2;
  }
  public float getTotalCartPrice() {
    return totalCartPrice;
  }

  public void setPriceToPay(float totalCartPrice) {
    this.totalCartPrice = totalCartPrice;
  }

  public List<CartDto> getCartOrders() {
    return cartOrders;
  }

  public void setCartOrders(List<CartDto> cartOrders) {
    this.cartOrders = cartOrders;
  }
}

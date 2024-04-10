package com.example.mosawebapp.cart.dto;

import java.util.List;

public class CartCheckoutDto {
    private float priceToPay;
    private List<CartDto> carts;

    public CartCheckoutDto(){}

  public CartCheckoutDto(List<CartDto> carts) {
    this.priceToPay = computePriceToPay(carts);
    this.carts = carts;
  }

  private float computePriceToPay(List<CartDto> carts){
      float price = 0;

      for(CartDto cart: carts){
        price += cart.getTotalPrice();
      }

      return price / 2;
  }
  public float getPriceToPay() {
    return priceToPay;
  }

  public void setPriceToPay(float priceToPay) {
    this.priceToPay = priceToPay;
  }

  public List<CartDto> getCarts() {
    return carts;
  }

  public void setCarts(List<CartDto> carts) {
    this.carts = carts;
  }
}

package com.example.mosawebapp.cart.dto;

import java.util.List;

public class CheckoutForm {
  private List<String> ids;

  public CheckoutForm(){}

  public CheckoutForm(List<String> ids) {
    this.ids = ids;
  }

  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }
}

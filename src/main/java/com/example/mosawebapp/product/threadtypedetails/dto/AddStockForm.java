package com.example.mosawebapp.product.threadtypedetails.dto;

public class AddStockForm {
  private Long stocks;
  private String id;

  public AddStockForm(){}

  public AddStockForm(Long stocks, String id) {
    this.stocks = stocks;
    this.id = id;
  }

  public Long getStocks() {
    return stocks;
  }

  public void setStocks(Long stocks) {
    this.stocks = stocks;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}

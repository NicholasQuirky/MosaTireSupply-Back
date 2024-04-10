package com.example.mosawebapp.product.threadtypedetails.dto;

import org.springframework.lang.Nullable;

public class ThreadTypeDetailsForm {
  private String threadType;
  private String width;
  private String aspectRatio;
  private String diameter;
  private String sidewall;
  private String plyRating;
  private float price;
  @Nullable
  private Long stocks;

  public ThreadTypeDetailsForm(){}

  public ThreadTypeDetailsForm(String threadType, String width, String aspectRatio, String diameter,
      String sidewall, String plyRating, float price, @Nullable Long stocks) {
    this.threadType = threadType;
    this.width = width;
    this.aspectRatio = aspectRatio;
    this.diameter = diameter;
    this.sidewall = sidewall;
    this.plyRating = plyRating;
    this.price = price;
    this.stocks = stocks;
  }

  public String getThreadType() {
    return threadType;
  }

  public void setThreadType(String threadType) {
    this.threadType = threadType;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public String getAspectRatio() {
    return aspectRatio;
  }

  public void setAspectRatio(String aspectRatio) {
    this.aspectRatio = aspectRatio;
  }

  public String getDiameter() {
    return diameter;
  }

  public void setDiameter(String diameter) {
    this.diameter = diameter;
  }

  public String getSidewall() {
    return sidewall;
  }

  public void setSidewall(String sidewall) {
    this.sidewall = sidewall;
  }

  public String getPlyRating() {
    return plyRating;
  }

  public void setPlyRating(String plyRating) {
    this.plyRating = plyRating;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  @Nullable
  public Long getStocks() {
    return stocks;
  }

  public void setStocks(@Nullable Long stocks) {
    this.stocks = stocks;
  }
}

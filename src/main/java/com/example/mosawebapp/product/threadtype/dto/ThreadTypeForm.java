package com.example.mosawebapp.product.threadtype.dto;

import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import org.springframework.lang.Nullable;

public class ThreadTypeForm {
  private String brand;
  private String type;
  private String imageUrl;
  @Nullable
  private String description;

  public ThreadTypeForm(){}

  public ThreadTypeForm(String brand, String type, String imageUrl, @Nullable String description) {
    this.brand = brand;
    this.type = type;
    this.imageUrl = imageUrl;
    this.description = description;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Nullable
  public String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }
}

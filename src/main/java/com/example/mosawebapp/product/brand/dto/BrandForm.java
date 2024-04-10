package com.example.mosawebapp.product.brand.dto;

public class BrandForm {
  private String brandName;
  private String imageUrl;

  public BrandForm(){}

  public BrandForm(String brandName, String imageUrl) {
    this.brandName = brandName;
    this.imageUrl = imageUrl;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}

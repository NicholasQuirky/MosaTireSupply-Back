package com.example.mosawebapp.product.threadtype.dto;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ThreadTypeDto {
  private String id;
  private String dateCreated;
  private String brandName;
  private String type;
  private float minPrice;
  private float maxPrice;
  private int rating;
  private String imageUrl;
  private String description;
  private List<ThreadTypeDetailsDto> detail;

  public ThreadTypeDto(){}

  public ThreadTypeDto(String id, String dateCreated, String type, int rating, String imageUrl, String description) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.type = type;
    this.rating = rating;
    this.imageUrl = imageUrl;
    this.description = description;
  }

  public ThreadTypeDto(ThreadType threadType, List<ThreadTypeDetails> details){
    this.id = threadType.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(threadType.getDateCreated());
    this.type = threadType.getType();
    this.minPrice = getMinimumPrice(details);
    this.maxPrice = getMaximumPrice(details);
    this.rating = threadType.getRating();
    this.imageUrl = threadType.getImageUrl();
    this.description = threadType.getDescription();
    this.detail = ThreadTypeDetailsDto.buildFromEntitiesV2(threadType, details);
    this.brandName = threadType.getBrand().getName();
  }

  public ThreadTypeDto(ThreadType threadType){
    this.id = threadType.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(threadType.getDateCreated());
    this.type = threadType.getType();
    this.minPrice = (float) 0;
    this.maxPrice = (float) 0;
    this.rating = threadType.getRating();
    this.imageUrl = threadType.getImageUrl();
    this.description = threadType.getDescription();
    this.detail = null;
    this.brandName = threadType.getBrand().getName();
  }

  private float getMinimumPrice(List<ThreadTypeDetails> details){
    float lowestPrice = details.get(0).getPrice();

    for(ThreadTypeDetails d: details){
      if(d.getPrice() < lowestPrice){
        lowestPrice = d.getPrice();
      }
    }

    return lowestPrice;
  }

  private float getMaximumPrice(List<ThreadTypeDetails> details){
    float highestPrice = details.get(0).getPrice();

    for(ThreadTypeDetails d: details){
      if(d.getPrice() > highestPrice){
        highestPrice = d.getPrice();
      }
    }

    return highestPrice;
  }

  private float getMinimumPriceAsDto(List<ThreadTypeDetailsDto> details){
    float lowestPrice = details.get(0).getPrice();

    for(ThreadTypeDetailsDto d: details){
      if(d.getPrice() < lowestPrice){
        lowestPrice = d.getPrice();
      }
    }

    return lowestPrice;
  }

  private float getMaximumPriceAsDto(List<ThreadTypeDetailsDto> details){
    float highestPrice = details.get(0).getPrice();

    for(ThreadTypeDetailsDto d: details){
      if(d.getPrice() > highestPrice){
        highestPrice = d.getPrice();
      }
    }

    return highestPrice;
  }

  public static ThreadTypeDto buildFromEntity(ThreadType type){
    return new ThreadTypeDto(type.getId(), DateTimeFormatter.get_MMDDYYY_Format(type.getDateCreated()), type.getType(),
        type.getRating(), type.getImageUrl(), type.getDescription());
  }

  public static List<ThreadTypeDto> buildFromEntities(List<ThreadType> types){
    List<ThreadTypeDto> dto = new ArrayList<>();

    for(ThreadType type: types){
      dto.add(buildFromEntity(type));
    }

    return dto;
  }

  public static List<ThreadTypeDto> buildFromEntitiesV2(List<ThreadType> types){
    List<ThreadTypeDto> dto = new ArrayList<>();

    for(ThreadType type: types){
      dto.add(new ThreadTypeDto(type));
    }

    return dto;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public float getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(float minPrice) {
    this.minPrice = minPrice;
  }

  public float getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(float maxPrice) {
    this.maxPrice = maxPrice;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ThreadTypeDetailsDto> getDetail() {
    return detail;
  }

  public void setDetail(List<ThreadTypeDetailsDto> detail) {
    this.detail = detail;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }
}

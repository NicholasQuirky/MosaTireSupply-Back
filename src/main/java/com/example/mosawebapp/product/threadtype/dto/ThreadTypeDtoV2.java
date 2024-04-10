package com.example.mosawebapp.product.threadtype.dto;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThreadTypeDtoV2 {
  private String id;
  private String dateCreated;
  private String brandName;
  private String type;
  private int rating;
  private String imageUrl;
  private String description;

  public ThreadTypeDtoV2(){}

  public ThreadTypeDtoV2(String id, String dateCreated, String brandName, String type, int rating,
      String imageUrl, String description) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.brandName = brandName;
    this.type = type;
    this.rating = rating;
    this.imageUrl = imageUrl;
    this.description = description;
  }

  public static ThreadTypeDtoV2 buildFromEntity(ThreadType type){
    return new ThreadTypeDtoV2(type.getId(), DateTimeFormatter.get_MMDDYYY_Format(type.getDateCreated()), type.getBrand().getName(), type.getType(),
        type.getRating(), type.getImageUrl(), type.getDescription());
  }

  public static List<ThreadTypeDtoV2> buildFromEntities(List<ThreadType> types){
    List<ThreadTypeDtoV2> dto = new ArrayList<>();

    for(ThreadType type: types){
      dto.add(buildFromEntity(type));
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

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
}

package com.example.mosawebapp.product.brand.dto;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDtoV2;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BrandDto {
  private String id;
  private String dateCreated;
  private String name;
  private String imageUrl;
  private List<ThreadTypeDtoV2> threadType;

  public BrandDto(){}

  public BrandDto(String id, String dateCreated, String name, String imageUrl) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.name = name;
    this.imageUrl = imageUrl;
  }

  public BrandDto(Brand brand, List<ThreadTypeDtoV2> threadType){
    this.id = brand.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(brand.getDateCreated());
    this.name = brand.getName();
    this.imageUrl = brand.getImageUrl();
    this.threadType = threadType;
  }


  public static BrandDto buildFromEntity(Brand brand){
    return new BrandDto(brand.getId(), DateTimeFormatter.get_MMDDYYY_Format(brand.getDateCreated()), brand.getName(), brand.getImageUrl());
  }

  public static List<BrandDto> buildFromEntities(List<Brand> brandList){
    List<BrandDto> dto = new ArrayList<>();

    for(Brand brand: brandList){
      dto.add(buildFromEntity(brand));
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ThreadTypeDtoV2> getThreadType() {
    return threadType;
  }

  public void setThreadType(List<ThreadTypeDtoV2> threadType) {
    this.threadType = threadType;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}

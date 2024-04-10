package com.example.mosawebapp.product.threadtypedetails.dto;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

public class ThreadTypeDetailsDto {
  private String id;
  private String dateCreated;
  private String threadType;
  private String width;
  private String aspectRatio;
  private String diameter;
  private String sidewall;
  private String plyRating;
  private float price;
  private Long stocks;

  public ThreadTypeDetailsDto(){}

  public ThreadTypeDetailsDto(String id, String dateCreated, String threadType, String width, String aspectRatio,
      String diameter, String sidewall, String plyRating, float price, Long stocks) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.threadType = threadType;
    this.width = width;
    this.aspectRatio = aspectRatio;
    this.diameter = diameter;
    this.sidewall = sidewall;
    this.plyRating = plyRating;
    this.price = price;
    this.stocks = stocks;
  }

  public ThreadTypeDetailsDto(ThreadType threadType, ThreadTypeDetails details){
    this.id = details.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(details.getDateCreated());
    this.threadType = threadType.getType();
    this.width = details.getWidth();
    this.aspectRatio = details.getAspectRatio();
    this.diameter = details.getDiameter();
    this.sidewall = details.getSidewall();
    this.plyRating = details.getPlyRating();
    this.price = details.getPrice();
    this.stocks = details.getStocks();
  }

  public static ThreadTypeDetailsDto buildFromEntity(ThreadTypeDetails details){
    return new ThreadTypeDetailsDto(details.getId(), DateTimeFormatter.get_MMDDYYY_Format(details.getDateCreated()),
        details.getThreadType().getType(), details.getWidth(), details.getAspectRatio(),
        details.getDiameter(), details.getSidewall(), details.getPlyRating(), details.getPrice(), details.getStocks());
  }

  public static List<ThreadTypeDetailsDto> buildFromEntities(List<ThreadTypeDetails> details) {
    List<ThreadTypeDetailsDto> dto = new ArrayList<>();

    for (ThreadTypeDetails detail : details) {
      dto.add(buildFromEntity(detail));
    }
    return dto;
  }

  public static List<ThreadTypeDetailsDto> buildFromEntitiesV2(ThreadType type, List<ThreadTypeDetails> details){
    List<ThreadTypeDetailsDto> dto = new ArrayList<>();

    for(ThreadTypeDetails detail : details){
      dto.add(new ThreadTypeDetailsDto(type, detail));
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

  public Long getStocks() {
    return stocks;
  }

  public void setStocks(Long stocks) {
    this.stocks = stocks;
  }

  public String getThreadType() {
    return threadType;
  }

  public void setThreadType(String threadType) {
    this.threadType = threadType;
  }
}

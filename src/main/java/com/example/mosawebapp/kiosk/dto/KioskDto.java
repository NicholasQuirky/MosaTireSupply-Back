package com.example.mosawebapp.kiosk.dto;

import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.domain.KioskOrderStatus;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KioskDto {
  private String kioskId;
  private String dateCreated;
  private String kioskToken;
  private Long queueingNumber;
  private String brandName;
  private String threadType;
  private String imageUrl;
  private ThreadTypeDetailsDto details;
  private long quantity;
  private float totalPrice;

  public KioskDto(){}

  public KioskDto(String kioskId, String dateCreated, String kioskToken, String brandName,
      String threadType, String imageUrl, ThreadTypeDetailsDto details, long quantity,
      float totalPrice) {
    this.kioskId = kioskId;
    this.dateCreated = dateCreated;
    this.kioskToken = kioskToken;
    this.brandName = brandName;
    this.threadType = threadType;
    this.imageUrl = imageUrl;
    this.details = details;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  public KioskDto(Kiosk kiosk){
    this.kioskId = kiosk.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(kiosk.getDateCreated());
    this.kioskToken = kiosk.getToken();
    this.brandName = kiosk.getType().getBrand().getName();
    this.threadType = kiosk.getType().getType();
    this.imageUrl = kiosk.getType().getImageUrl();
    this.details = ThreadTypeDetailsDto.buildFromEntity(kiosk.getDetails());
    this.quantity = kiosk.getQuantity();
    this.totalPrice = kiosk.getQuantity() * kiosk.getDetails().getPrice();
    this.queueingNumber = kiosk.getQueueingNumber();
  }

  public static List<KioskDto> buildFromEntities(List<Kiosk> kiosks){
    List<KioskDto> dto = new ArrayList<>();

    for(Kiosk kiosk: kiosks){
      dto.add(new KioskDto(kiosk));
    }

    return dto;
  }
  public String getKioskId() {
    return kioskId;
  }

  public void setKioskId(String kioskId) {
    this.kioskId = kioskId;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getKioskToken() {
    return kioskToken;
  }

  public void setKioskToken(String kioskToken) {
    this.kioskToken = kioskToken;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getThreadType() {
    return threadType;
  }

  public void setThreadType(String threadType) {
    this.threadType = threadType;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public ThreadTypeDetailsDto getDetails() {
    return details;
  }

  public void setDetails(ThreadTypeDetailsDto details) {
    this.details = details;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public float getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(float totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Long getQueueingNumber() {
    return queueingNumber;
  }

  public void setQueueingNumber(Long queueingNumber) {
    this.queueingNumber = queueingNumber;
  }
}

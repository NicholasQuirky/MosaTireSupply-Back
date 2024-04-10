package com.example.mosawebapp.onsite_order.dto;

import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OnsiteOrderDto {
  private String orderId;
  private String dateCreated;
  private String brandName;
  private String threadType;
  private String imageUrl;
  private ThreadTypeDetailsDto details;
  private long quantity;
  private float totalPrice;

  public OnsiteOrderDto(){}

  public OnsiteOrderDto(String orderId, String dateCreated, String brandName, String threadType,
      String imageUrl, ThreadTypeDetailsDto details, long quantity, float totalPrice) {
    this.orderId = orderId;
    this.dateCreated = dateCreated;
    this.brandName = brandName;
    this.threadType = threadType;
    this.imageUrl = imageUrl;
    this.details = details;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  public OnsiteOrderDto(OnsiteOrder order){
    this.orderId = order.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(order.getDateCreated());
    this.brandName = order.getType().getBrand().getName();
    this.threadType = order.getType().getType();
    this.imageUrl = order.getType().getImageUrl();
    this.details = ThreadTypeDetailsDto.buildFromEntity(order.getDetails());
    this.quantity = order.getQuantity();
    this.totalPrice = order.getQuantity() * order.getDetails().getPrice();
  }

  public static List<OnsiteOrderDto> buildFromEntities(List<OnsiteOrder> orders){
    List<OnsiteOrderDto> dto = new ArrayList<>();

    for(OnsiteOrder order: orders){
      dto.add(new OnsiteOrderDto(order));
    }

    return dto;
  }
  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
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
}

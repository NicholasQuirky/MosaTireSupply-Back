package com.example.mosawebapp.all_orders.dto;

import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class OrdersDto {
  private String orderId;
  private String dateOrdered;
  private OrderStatus status;
  private String customerName;
  private String customerEmail;
  private String paymentMethod;
  private String referenceNumber;
  private OrderType orderType;
  private float orderTotalPrice;
  private String kioskToken;
  private Long queueingNumber;
  private List<CartDto> onlineOrders;
  private List<KioskDto> kioskOrders;
  private List<OnsiteOrderDto> onsiteOrders;

  public OrdersDto(){}

  public OrdersDto(String orderId, String dateOrdered, List<CartDto> onlineOrders) {
    this.orderId = orderId;
    this.dateOrdered = dateOrdered;
    this.onlineOrders = onlineOrders;
  }

  public OrdersDto(Orders orders){
    this.orderId = orders.getId();
    this.dateOrdered = DateTimeFormatter.get_MMDDYYY_Format(orders.getDateCreated());
    this.referenceNumber = orders.getReferenceNumber();
    this.orderType = orders.getOrderType();
    this.paymentMethod = orders.getPaymentMethod();
  }

  public OrdersDto(Orders orders, List<CartDto> onlineOrders, List<KioskDto> kioskOrders, List<OnsiteOrderDto> onsiteOrders){
    this.paymentMethod = orders.getPaymentMethod();
    this.orderId = orders.getOrderId();
    this.dateOrdered = DateTimeFormatter.get_MMDDYYY_Format(orders.getDateCreated());
    this.referenceNumber = orders.getReferenceNumber();
    this.status = orders.getOrderStatus();

    if(onlineOrders != null){
      this.customerName = findOnlineOrderCustomerName(onlineOrders);
      this.customerEmail = findOnlineOrderCustomerEmail(onlineOrders);
      this.orderType = orders.getOrderType();
      this.onlineOrders = onlineOrders;
      this.orderTotalPrice = computeTotalPriceForOnlineOrders(onlineOrders);
    }

    if(kioskOrders != null){
      this.orderType = orders.getOrderType();
      this.kioskOrders = kioskOrders;
      this.kioskToken = findKioskTokenForKiosk(kioskOrders);
      this.queueingNumber = findQueueingNumberForKiosk(kioskOrders);
      this.orderTotalPrice = computeTotalPriceForKioskOrders(kioskOrders);
    }

    if(onsiteOrders != null){
      this.orderType = orders.getOrderType();
      this.onsiteOrders = onsiteOrders;
      this.orderTotalPrice = computeTotalPriceForOnsiteOrders(onsiteOrders);
    }
  }
  private String findOnlineOrderCustomerName(List<CartDto> carts){
    return carts.stream()
        .filter(dto -> !dto.getCustomerName().isEmpty())
        .findFirst()
        .map(CartDto::getCustomerName)
        .orElse("");
  }

  private String findOnlineOrderCustomerEmail(List<CartDto> carts){
    return carts.stream()
        .filter(dto -> !dto.getCustomerEmail().isEmpty())
        .findFirst()
        .map(CartDto::getCustomerEmail)
        .orElse("");
  }

  private Long findQueueingNumberForKiosk(List<KioskDto> kiosks){
    return kiosks.stream()
        .filter(dto -> dto.getQueueingNumber() != null)
        .findFirst()
        .map(KioskDto::getQueueingNumber)
        .orElse(null);
  }

  private String findKioskTokenForKiosk(List<KioskDto> kiosks){
    return kiosks.stream()
        .filter(dto -> !dto.getKioskToken().isEmpty())
        .findFirst()
        .map(KioskDto::getKioskToken)
        .orElse(null);
  }

  private float computeTotalPriceForOnlineOrders(List<CartDto> onlineOrders){
    float price = 0;

    for(CartDto dto: onlineOrders){
      price += dto.getTotalPrice();
    }

    return price;
  }

  private float computeTotalPriceForKioskOrders(List<KioskDto> kioskOrders){
    float price = 0;

    for(KioskDto dto: kioskOrders){
      price += dto.getTotalPrice();
    }

    return price;
  }

  private float computeTotalPriceForOnsiteOrders(List<OnsiteOrderDto> onsiteOrders){
    float price = 0;

    for(OnsiteOrderDto dto: onsiteOrders){
      price += dto.getTotalPrice();
    }

    return price;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getDateOrdered() {
    return dateOrdered;
  }

  public void setDateOrdered(String dateOrdered) {
    this.dateOrdered = dateOrdered;
  }

  public List<CartDto> getOnlineOrders() {
    return onlineOrders;
  }

  public void setOnlineOrders(List<CartDto> onlineOrders) {
    this.onlineOrders = onlineOrders;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public float getOrderTotalPrice() {
    return orderTotalPrice;
  }

  public void setOrderTotalPrice(float orderTotalPrice) {
    this.orderTotalPrice = orderTotalPrice;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public List<KioskDto> getKioskOrders() {
    return kioskOrders;
  }

  public void setKioskOrders(List<KioskDto> kioskOrders) {
    this.kioskOrders = kioskOrders;
  }

  public List<OnsiteOrderDto> getOnsiteOrders() {
    return onsiteOrders;
  }

  public void setOnsiteOrders(
      List<OnsiteOrderDto> onsiteOrders) {
    this.onsiteOrders = onsiteOrders;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  public Long getQueueingNumber() {
    return queueingNumber;
  }

  public void setQueueingNumber(Long queueingNumber) {
    this.queueingNumber = queueingNumber;
  }

  public String getKioskToken() {
    return kioskToken;
  }

  public void setKioskToken(String kioskToken) {
    this.kioskToken = kioskToken;
  }
}

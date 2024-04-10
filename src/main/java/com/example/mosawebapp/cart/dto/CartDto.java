package com.example.mosawebapp.cart.dto;

import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartDto {
  private String cartOrderId;
  private String dateCreated;
  private String customerName;
  private String customerEmail;
  private String brandName;
  private String threadType;
  private String imageUrl;
  private ThreadTypeDetailsDto details;
  private long quantity;
  private float totalPrice;
  private OrderStatus orderStatus;

  public CartDto(){}

  public CartDto(String cartOrderId, String dateCreated, String brandName, String threadType,
      ThreadTypeDetails details, long quantity, float totalPrice) {
    this.cartOrderId = cartOrderId;
    this.dateCreated = dateCreated;
    this.brandName = brandName;
    this.threadType = threadType;
    this.details = ThreadTypeDetailsDto.buildFromEntity(details);
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  public CartDto(Cart cart, OrderStatus status){
    this.cartOrderId = cart.getId();
    this.dateCreated = DateTimeFormatter.get_MMDDYYY_Format(cart.getDateCreated());
    this.customerName = validateCustomerName(cart);
    this.customerEmail = cart.getAccount().getEmail();
    this.brandName = cart.getType().getBrand().getName();
    this.imageUrl = cart.getType().getImageUrl();
    this.threadType = cart.getType().getType();
    this.details = ThreadTypeDetailsDto.buildFromEntity(cart.getDetails());
    this.quantity = cart.getQuantity();
    this.totalPrice = cart.getQuantity() * details.getPrice();
    this.orderStatus = status;
  }

  public static List<CartDto> buildFromEntitiesForCheckout(List<Cart> carts){
    List<CartDto> dto = new ArrayList<>();

    for(Cart cart: carts){
      dto.add(new CartDto(cart, OrderStatus.FOR_CHECKOUT));
    }

    return dto;
  }

  public String validateCustomerName(Cart cart){
    if(cart.getAccount().getFullName() == null || cart.getAccount().getFullName().isEmpty()){
      return "";
    }

    return cart.getAccount().getFullName();
  }

  public String getCartOrderId() {
    return cartOrderId;
  }

  public void setCartOrderId(String cartOrderId) {
    this.cartOrderId = cartOrderId;
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

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }
}

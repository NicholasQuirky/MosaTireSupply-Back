package com.example.mosawebapp.all_orders.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import java.util.Date;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.apache.poi.xwpf.usermodel.Borders;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Orders {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  @Enumerated(EnumType.STRING)
  private OrderType orderType;
  @Column
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;
  @Column
  private String referenceNumber;
  @Column
  private String paymentMethod;
  @OneToOne
  @JoinColumn(name = "cart_id")
  private Cart cart;
  @OneToOne
  @JoinColumn(name = "kiosk_id")
  private Kiosk kiosk;
  @OneToOne
  @JoinColumn(name = "onsite_order_id")
  private OnsiteOrder onsiteOrder;
  @Column
  private String orderId;

  public Orders(){}
  public Orders(OrderType orderType, OrderStatus status, String referenceNumber, String paymentMethod,
      Cart cart, Kiosk kiosk, OnsiteOrder order, String orderId) {
    this.paymentMethod = paymentMethod;
    this.orderType = orderType;
    this.orderStatus = status;
    this.referenceNumber = referenceNumber;
    this.orderId = orderId;

    if(cart != null){
      this.cart = cart;
    }

    if(kiosk != null){
      this.kiosk = kiosk;
    }

    if(order != null){
      this.onsiteOrder = order;
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public Kiosk getKiosk() {
    return kiosk;
  }

  public void setKiosk(Kiosk kiosk) {
    this.kiosk = kiosk;
  }

  public OnsiteOrder getOnsiteOrder() {
    return onsiteOrder;
  }

  public void setOnsiteOrder(OnsiteOrder onsiteOrder) {
    this.onsiteOrder = onsiteOrder;
  }
}

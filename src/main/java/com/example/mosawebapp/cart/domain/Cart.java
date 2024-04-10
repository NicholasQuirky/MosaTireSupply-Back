package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Cart {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @OneToOne
  @JoinColumn(name = "customer_id")
  private Account account;
  @OneToOne
  @JoinColumn(name = "thread_type_id")
  private ThreadType type;
  @OneToOne
  @JoinColumn(name = "details_id")
  private ThreadTypeDetails details;
  @Column
  private long quantity;
  @Column
  private float totalPrice;
  @Column
  private boolean isCheckedOut;
  @Column
  private boolean isPaid;
  @Column
  private boolean isOrderNow;

  public Cart(){}

  public Cart(Account account, ThreadType type, ThreadTypeDetails details, long quantity,
      float totalPrice, boolean isCheckedOut, boolean isPaid) {
    this.account = account;
    this.type = type;
    this.details = details;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.isCheckedOut = isCheckedOut;
    this.isPaid = isPaid;
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

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public ThreadType getType() {
    return type;
  }

  public void setType(ThreadType type) {
    this.type = type;
  }

  public ThreadTypeDetails getDetails() {
    return details;
  }

  public void setDetails(ThreadTypeDetails details) {
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

  public boolean isCheckedOut() {
    return isCheckedOut;
  }

  public void setCheckedOut(boolean checkedOut) {
    isCheckedOut = checkedOut;
  }

  public boolean isPaid() {
    return isPaid;
  }

  public void setPaid(boolean paid) {
    isPaid = paid;
  }

  public boolean isOrderNow() {
    return isOrderNow;
  }

  public void setOrderNow(boolean orderNow) {
    isOrderNow = orderNow;
  }
}

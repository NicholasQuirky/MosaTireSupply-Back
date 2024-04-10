package com.example.mosawebapp.cart.dto;

import java.util.List;

public class ReferenceNumberForm {
  private String refNo;
  private String paymentMethod;
  private List<String> ids;

  public ReferenceNumberForm(){}

  public ReferenceNumberForm(String refNo, List<String> ids, String paymentMethod) {
    this.refNo = refNo;
    this.ids = ids;
    this.paymentMethod = paymentMethod;
  }

  public String getRefNo() {
    return refNo;
  }

  public void setRefNo(String refNo) {
    this.refNo = refNo;
  }

  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }
}

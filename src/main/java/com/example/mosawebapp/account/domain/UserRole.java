package com.example.mosawebapp.account.domain;

import java.util.Arrays;
import java.util.List;

public enum UserRole {
  ADMINISTRATOR("Administrator"),
  PRODUCT_MANAGER("Product Manager"),
  CONTENT_MANAGER("Content Manager"),
  ORDER_MANAGER("Order Manager"),
  CUSTOMER("Customer");

  String label;
  UserRole(String label) {
   this.label = label;
  }

  public static List<UserRole> getRolesList(){
    return Arrays.asList(ADMINISTRATOR, PRODUCT_MANAGER, CONTENT_MANAGER, ORDER_MANAGER, CUSTOMER);
  }

  public static List<String> getRoleLabelAsList(){
    return Arrays.asList("Administrator", "Product Manager", "Content Manager", "Order Manager", "Customer");
  }
}

package com.example.mosawebapp.account.dto;

import com.example.mosawebapp.account.domain.UserRole;
import org.springframework.lang.Nullable;

public class AccountUpdateForm {
  private String fullName;
  private String email;
  private String contactNumber;
  @Nullable
  private String address;
  @Nullable
  private UserRole userRole;

  public AccountUpdateForm(){}
  public AccountUpdateForm(String fullName, String email, String contactNumber, @Nullable String address, @Nullable UserRole userRole) {
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.userRole = userRole;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  @Nullable
  public String getAddress() {
    return address;
  }

  public void setAddress(@Nullable String address) {
    this.address = address;
  }

  @Nullable
  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(@Nullable UserRole userRole) {
    this.userRole = userRole;
  }
}

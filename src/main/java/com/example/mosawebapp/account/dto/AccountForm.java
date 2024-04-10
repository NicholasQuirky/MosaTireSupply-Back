package com.example.mosawebapp.account.dto;

import com.example.mosawebapp.account.domain.UserRole;
import org.springframework.lang.Nullable;

public class AccountForm {
  private String email;
  private String fullName;
  private String contactNumber;
  @Nullable
  private String address;
  private String password;
  private String confirmPassword;
  @Nullable
  private UserRole userRole;

  public AccountForm(){}
  public AccountForm(String fullName, String email, String contactNumber, @Nullable String address,
      String password, String confirmPassword, @Nullable UserRole userRole) {
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.password = password;
    this.confirmPassword = confirmPassword;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  @Nullable
  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(@Nullable UserRole userRole) {
    this.userRole = userRole;
  }
}

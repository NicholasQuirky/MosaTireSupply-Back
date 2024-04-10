package com.example.mosawebapp.scheduling.dto;

import org.springframework.lang.Nullable;

public class ScheduleForm {
  private String firstName;
  private String lastName;
  private String email;
  private String contactNumber;
  private String address;
  private String date;
  @Nullable
  private String comments;
  private boolean isApproved = false;

  public ScheduleForm(){}

  public ScheduleForm(String firstName, String lastName, String email, String contactNumber,
      String address, String date, @Nullable String comments) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.date = date;
    this.comments = comments;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Nullable
  public String getComments() {
    return comments;
  }

  public void setComments(@Nullable String comments) {
    this.comments = comments;
  }

  public boolean isApproved() {
    return isApproved;
  }

  public void setApproved(boolean approved) {
    isApproved = approved;
  }
}

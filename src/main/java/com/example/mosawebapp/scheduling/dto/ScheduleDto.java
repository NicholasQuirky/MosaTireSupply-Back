package com.example.mosawebapp.scheduling.dto;

import com.example.mosawebapp.scheduling.domain.Schedule;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;

public class ScheduleDto {
  private String id;
  private String customerName;
  private String customerEmail;
  private String customerContactNumber;
  private String customerAddress;
  private String dateScheduled;
  @Nullable
  private String comments;
  private boolean isApproved;

  public ScheduleDto(){}

  public ScheduleDto(String id, String customerName, String customerEmail,
      String customerContactNumber, String customerAddress, String dateScheduled, @Nullable String comments, boolean isApproved) {
    this.id = id;
    this.customerName = customerName;
    this.customerEmail = customerEmail;
    this.customerContactNumber = customerContactNumber;
    this.customerAddress = customerAddress;
    this.dateScheduled = dateScheduled;
    this.comments = comments;
    this.isApproved = isApproved;
  }

  public static ScheduleDto buildFromEntity(Schedule schedule){
    return new ScheduleDto(
        schedule.getId(), schedule.getOrderedBy().getFullName(), schedule.getOrderedBy().getEmail(),
        schedule.getOrderedBy().getContactNumber(), schedule.getOrderedBy().getAddress(), schedule.getDateScheduled(),
        schedule.getComments(), schedule.isApproved());
  }

  public static List<ScheduleDto> buildFromEntities(List<Schedule> schedules){
    List<ScheduleDto> dtos = new ArrayList<>();

    for(Schedule schedule: schedules){
      dtos.add(buildFromEntity(schedule));
    }

    return dtos;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getCustomerContactNumber() {
    return customerContactNumber;
  }

  public void setCustomerContactNumber(String customerContactNumber) {
    this.customerContactNumber = customerContactNumber;
  }

  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  public String getDateScheduled() {
    return dateScheduled;
  }

  public void setDateScheduled(String dateScheduled) {
    this.dateScheduled = dateScheduled;
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

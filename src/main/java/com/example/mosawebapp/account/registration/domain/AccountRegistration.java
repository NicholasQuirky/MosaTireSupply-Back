package com.example.mosawebapp.account.registration.domain;

import com.example.mosawebapp.account.domain.AccountStatus;
import com.example.mosawebapp.account.domain.UserRole;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class AccountRegistration {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String fullName;
  @Column
  private String email;
  @Column
  private String contactNumber;
  @Column
  private String address;
  @Column
  private String password;
  @Column
  @Enumerated(EnumType.STRING)
  private UserRole userRole;
  @Column
  private long registerOtp;
  @Column
  @Enumerated(EnumType.STRING)
  private AccountStatus status;

  public AccountRegistration(){}
  public AccountRegistration(String fullName, String email, String contactNumber,
      String address, String password, UserRole userRole) {
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.password = password;
    this.userRole = userRole;
  }

  public AccountRegistration(String fullName, String email, String contactNumber, String password, UserRole userRole) {
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.password = password;
    this.userRole = userRole;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public long getRegisterOtp() {
    return registerOtp;
  }

  public void setRegisterOtp(long registerOtp) {
    this.registerOtp = registerOtp;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }
}

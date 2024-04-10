package com.example.mosawebapp.account.domain;

import com.example.mosawebapp.validate.Validate;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.beans.factory.annotation.Value;

@Entity
public class Account {
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
  private long loginOtp;
  @Column
  private long changePasswordOtp;
  @Column
  private String changePasswordToken;
  @Column
  private boolean isOrdering;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
  @JoinTable(name = "accounts_roles", joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Role> roles;

  public Account(){}
  public Account(String fullName, String email, String contactNumber, String address,
      String password, UserRole userRole) {
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.password = password;
    this.userRole = userRole;
  }

  public Account(String fullName, String email, String contactNumber,
      String password, UserRole userRole) {
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.password = password;
    this.userRole = userRole;
  }

  public Account(String fullName, long loginOtp, long changePasswordOtp) {
    this.fullName = fullName;

    if(loginOtp != 0)
      this.loginOtp = loginOtp;

    if(changePasswordOtp != 0)
      this.changePasswordOtp = changePasswordOtp;
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

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public long getLoginOtp() {
    return loginOtp;
  }

  public void setLoginOtp(long loginOtp) {
    this.loginOtp = loginOtp;
  }

  public long getChangePasswordOtp() {
    return changePasswordOtp;
  }

  public void setChangePasswordOtp(long changePasswordOtp) {
    this.changePasswordOtp = changePasswordOtp;
  }
  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public String getChangePasswordToken() {
    return changePasswordToken;
  }

  public void setChangePasswordToken(String changePasswordToken) {
    this.changePasswordToken = changePasswordToken;
  }
  public boolean isOrdering() {
    return isOrdering;
  }

  public void setOrdering(boolean ordering) {
    isOrdering = ordering;
  }
}
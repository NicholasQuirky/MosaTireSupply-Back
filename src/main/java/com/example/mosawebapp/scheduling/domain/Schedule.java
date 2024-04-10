package com.example.mosawebapp.scheduling.domain;

import com.example.mosawebapp.account.domain.Account;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Schedule {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name="ordered_by")
  private Account orderedBy;
  @Column
  private String dateScheduled;
  @Column
  private String comments;
  @Column
  private boolean isApproved;

  public Schedule(){}
  public Schedule(Account orderedBy, String dateScheduled, String comments, boolean isApproved) {
    this.orderedBy = orderedBy;
    this.dateScheduled = dateScheduled;
    this.comments = comments;
    this.isApproved = isApproved;
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

  public Account getOrderedBy() {
    return orderedBy;
  }

  public void setOrderedBy(Account orderedBy) {
    this.orderedBy = orderedBy;
  }

  public String getDateScheduled() {
    return dateScheduled;
  }

  public void setDateScheduled(String dateScheduled) {
    this.dateScheduled = dateScheduled;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public boolean isApproved() {
    return isApproved;
  }

  public void setApproved(boolean approved) {
    isApproved = approved;
  }
}

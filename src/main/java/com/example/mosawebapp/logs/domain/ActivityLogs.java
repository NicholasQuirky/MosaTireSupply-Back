package com.example.mosawebapp.logs.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class ActivityLogs {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String actor;
  @Column
  private String activity;

  @Column
  private boolean isStaff;

  public ActivityLogs(){}

  public ActivityLogs(Date dateCreated, String actor, String activity, boolean isStaff) {
    this.dateCreated = dateCreated;
    this.actor = actor;
    this.activity = activity;
    this.isStaff = isStaff;
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

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public boolean isStaff() {
    return isStaff;
  }

  public void setStaff(boolean staff) {
    isStaff = staff;
  }
}

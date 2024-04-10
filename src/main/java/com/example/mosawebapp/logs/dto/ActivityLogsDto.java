package com.example.mosawebapp.logs.dto;

import com.example.mosawebapp.logs.domain.ActivityLogs;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogsDto {
  private String activityId;
  private String dateCreated;
  private String actor;
  private String activity;
  public ActivityLogsDto(){}

  public ActivityLogsDto(String activityId, String dateCreated, String actor, String activity) {
    this.activityId = activityId;
    this.dateCreated = dateCreated;
    this.actor = actor;
    this.activity = activity;
  }

  public static ActivityLogsDto buildFromEntity(ActivityLogs logs){
    return new ActivityLogsDto(logs.getId(), DateTimeFormatter.get_MMDDYYY_Format(logs.getDateCreated()), logs.getActor(), logs.getActivity());
  }

  public static List<ActivityLogsDto> buildFromEntities(List<ActivityLogs> logs){
    List<ActivityLogsDto> dto = new ArrayList<>();

    for(ActivityLogs log: logs){
      dto.add(buildFromEntity(log));
    }

    return dto;
  }
  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
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
}

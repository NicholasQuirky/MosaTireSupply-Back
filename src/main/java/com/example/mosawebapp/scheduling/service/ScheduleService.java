package com.example.mosawebapp.scheduling.service;

import com.example.mosawebapp.scheduling.domain.Schedule;
import com.example.mosawebapp.scheduling.dto.ScheduleForm;
import java.text.ParseException;

public interface ScheduleService {
  Schedule makeSchedule(ScheduleForm form);

  Schedule approveSchedule(String id, String token);
}

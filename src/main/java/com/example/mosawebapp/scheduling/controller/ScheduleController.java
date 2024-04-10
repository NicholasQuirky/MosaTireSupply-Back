package com.example.mosawebapp.scheduling.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.scheduling.dto.ScheduleDto;
import com.example.mosawebapp.scheduling.dto.ScheduleForm;
import com.example.mosawebapp.scheduling.service.ScheduleService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
  private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);
  private final ScheduleService scheduleService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;

  public ScheduleController(ScheduleService scheduleService,
      TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator) {
    this.scheduleService = scheduleService;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
  }

  @PostMapping(value="/makeSchedule")
  public ResponseEntity<?> makeSchedule(@RequestBody ScheduleForm form){
    logger.info("making a schedule service appointment by {}", form.getEmail());

    ScheduleDto dto = ScheduleDto.buildFromEntity(scheduleService.makeSchedule(form));

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Scheduled successfully", dto));
  }

  @GetMapping(value="/approveSchedule/{id}")
  public ResponseEntity<?> approveSchedule(@PathVariable("id") String id, @RequestHeader("Authorization") String header){
    logger.info("approving schedule {}", id);
    String token = header.replace("Bearer ", "");

    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException("Token Invalid/Expired");
    }

    ScheduleDto dto = ScheduleDto.buildFromEntity(scheduleService.approveSchedule(id, token));

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Approved Successfully", dto));
  }
}

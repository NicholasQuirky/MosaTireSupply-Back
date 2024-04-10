package com.example.mosawebapp.kiosk.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class KioskQueueingService {
  private static KioskQueueingService instance;
  private AtomicInteger tokenCounter = new AtomicInteger(0);
  private Date currentDate = new Date();

  private KioskQueueingService(){}

  public static synchronized KioskQueueingService getInstance(){
    if(instance == null){
      instance = new KioskQueueingService();
    }
    return instance;
  }
  public String generateToken() {
    Date now = new Date();
    if (!now.equals(currentDate)) {
      tokenCounter.set(0);
      currentDate = now;
    }
    return "MTS-" + String.format("%04d", tokenCounter.incrementAndGet());
  }
}

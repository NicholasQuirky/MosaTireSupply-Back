package com.example.mosawebapp.kiosk.domain;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public enum KioskOrderStatus {
  PROCESSING("Processing"),
  COMPLETED("Completed");

  String label;

  KioskOrderStatus(String label){this.label = label;}

  public static List<KioskOrderStatus> getAllStatus(){
    return Arrays.asList(PROCESSING, COMPLETED);
  }
}

package com.example.mosawebapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormatter {
  private DateTimeFormatter(){}

  public static String get_MMDDYYY_Format(Date date){
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    return dateFormat.format(date);
  }

  public static String get_YYYYMMDD_Format(Date date){
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    return dateFormat.format(date);
  }
}

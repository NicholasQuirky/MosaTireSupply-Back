package com.example.mosawebapp.utils;
import org.apache.commons.lang3.RandomStringUtils;
public class ReferenceNumberGenerator {
  private ReferenceNumberGenerator(){}

  public static String generateRefNumber(){

    return "MST-" + RandomStringUtils.randomAlphanumeric(10);
  }
}

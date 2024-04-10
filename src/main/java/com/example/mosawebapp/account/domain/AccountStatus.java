package com.example.mosawebapp.account.domain;

import java.util.Arrays;
import java.util.List;

public enum AccountStatus {
  ACTIVE("Active"),
  DEACTIVATED("Account Deactivated"),
  FOR_REGISTRATION("For Registration");

  String label;
  AccountStatus(String label) {
    this.label = label;
  }

  public static List<AccountStatus> getStatusList(){
    return Arrays.asList(ACTIVE, DEACTIVATED, FOR_REGISTRATION);
  }

  public static List<String> getStatusLabelList(){
    return Arrays.asList("Active", "Account Deactivated", "For Registration");
  }
}

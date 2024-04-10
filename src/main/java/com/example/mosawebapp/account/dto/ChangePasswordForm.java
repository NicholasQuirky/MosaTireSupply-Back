package com.example.mosawebapp.account.dto;

import org.springframework.lang.Nullable;

public class ChangePasswordForm {
  @Nullable
  private String oldPassword;
  private String newPassword;
  private String confirmPassword;

  public ChangePasswordForm(){}
  public ChangePasswordForm(@Nullable String oldPassword, String newPassword, String confirmPassword) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.confirmPassword = confirmPassword;
  }

  @Nullable
  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(@Nullable String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}

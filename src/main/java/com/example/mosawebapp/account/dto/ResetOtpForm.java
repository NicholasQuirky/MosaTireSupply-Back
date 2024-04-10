package com.example.mosawebapp.account.dto;

public class ResetOtpForm {
    private String id;
    private boolean isRegister; // for frontend, just "register" as the key

    public ResetOtpForm(){}

    public ResetOtpForm(String id, boolean isRegister) {
        this.id = id;
        this.isRegister = isRegister;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }
}

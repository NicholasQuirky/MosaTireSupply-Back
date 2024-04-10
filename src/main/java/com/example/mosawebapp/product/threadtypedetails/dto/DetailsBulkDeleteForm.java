package com.example.mosawebapp.product.threadtypedetails.dto;

import java.util.List;

public class DetailsBulkDeleteForm {
  private List<String> ids;

  public DetailsBulkDeleteForm(){}

  public DetailsBulkDeleteForm(List<String> ids) {
    this.ids = ids;
  }

  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }
}

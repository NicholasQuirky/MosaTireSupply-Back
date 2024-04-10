package com.example.mosawebapp.product.threadtype.service;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeForm;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeSearch;
import java.util.List;

public interface ThreadTypeService {
  ThreadTypeDto findThreadType(String id);
  List<ThreadTypeDto> findAllThreadTypes(String brand);
  List<ThreadTypeDto> addThreadType(String token, List<ThreadTypeForm> forms);
  ThreadTypeDto updateThreadType(String token, String id, ThreadTypeForm form);
  List<ThreadTypeDto> searchThreadType(String search);
  void deleteThreadType(String token, String id);
}

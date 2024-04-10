package com.example.mosawebapp.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
** Used mostly for testing
 */

@RestController
@RequestMapping("/mail")
public class MailController {
  @Autowired
  private MailService mailService;

  @PostMapping("/send/{email}")
  public ResponseEntity<?> sendEmail(@PathVariable("email") String email, @RequestBody MailStructure mailStructure){
    mailService.sendEmail(email, mailStructure);
    return ResponseEntity.ok("Email sent to " + email);
  }
}

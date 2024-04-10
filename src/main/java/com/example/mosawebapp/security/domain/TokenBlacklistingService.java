package com.example.mosawebapp.security.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.security.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistingService {
  private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistingService.class);

  @Autowired
  private TokenBlacklistRepository tokenBlacklistRepository;
  @Autowired
  private JwtGenerator jwtGenerator;
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private ActivityLogsService activityLogsService;

  public void addTokenToBlacklist(String token){
    logger.info("adding {} to blacklist", token);

    TokenBlacklist tokenBlacklist = tokenBlacklistRepository.findByToken(token);

    if(tokenBlacklist != null){
      throw new SecurityException("Token can no longer be used");
    } else {
      if(tokenBlacklistRepository.tokensAreOverTwenty()){
        tokenBlacklistRepository.deleteAll();
      }

      String id = jwtGenerator.getUserFromJWT(token);
      Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));

      activityLogsService.logoutActivity(account);
      tokenBlacklistRepository.save(new TokenBlacklist(token));
    }
  }

  public boolean isTokenBlacklisted(String token) {
    TokenBlacklist status = tokenBlacklistRepository.findByToken(token);

    return status != null;
  }
}

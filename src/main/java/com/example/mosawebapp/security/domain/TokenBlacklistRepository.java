package com.example.mosawebapp.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
  @Query("SELECT CASE WHEN COUNT(tbl) > 20 THEN true ELSE false END FROM TokenBlacklist tbl")
  boolean tokensAreOverTwenty();
  @Query(value = "DELETE from token_blacklist WHERE token = :token", nativeQuery = true)
  void deleteByToken(@Param("token") String token);
  TokenBlacklist findByToken(String token);
}

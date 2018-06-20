package com.gigsterous.auth.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

public class TokenServiceImplTest {

  @Mock
  private JdbcTokenStore tokenStore;

  @Mock
  private OAuth2AccessToken accessToken;

  @Mock
  private OAuth2RefreshToken refreshToken;

  private TokenService tokenService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    tokenService = new TokenServiceImpl(tokenStore);
  }

  @Test
  public void testGivenUsernameWhenRevokingTokensThenRevokeAllTokens() {
    // mock access token
    given(accessToken.getRefreshToken()).willReturn(refreshToken);

    List<OAuth2AccessToken> tokens = new ArrayList<>();
    tokens.add(accessToken);

    given(tokenStore.findTokensByUserName(eq("john@example.com"))).willReturn(tokens);

    tokenService.revokeTokens("john@example.com");

    verify(tokenStore).removeAccessToken(accessToken);
    verify(tokenStore).removeRefreshToken(refreshToken);
  }

}

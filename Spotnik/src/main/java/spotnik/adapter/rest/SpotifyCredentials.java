package spotnik.adapter.rest;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import spotnik.adapter.rest.dto.spotify.TokensDTO;

public class SpotifyCredentials {

  private static final Logger LOG = LoggerFactory.getLogger(SpotifyCredentials.class);

  private static final int REFRESH_THRESHOLD_MINUTES = 10;
  private static final String TOKEN_SCOPE = "user-read-playback-state%20user-modify-playback-state";

  private final WebClient webClient;

  private final String spotifyAppId;
  private final String redirectUri;

  private String accessToken = null;
  private LocalDateTime accessTokenExpirationDate = null;
  private String refreshToken = null;
  private String requestState;
  private Runnable onAuthenticatedCallback;

  public SpotifyCredentials(final WebClient.Builder webClientBuilder, final String spotifyAppId, final String spotifyAppSecret,
      final String redirectUri) {
    final String authorizationPayload = spotifyAppId + ":" + spotifyAppSecret;
    this.webClient = webClientBuilder
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(authorizationPayload.getBytes()))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .baseUrl("https://accounts.spotify.com/api/token")
        .build();
    this.spotifyAppId = spotifyAppId;
    this.redirectUri = redirectUri;
  }

  String requestSpotifyAuthorization() {
    LOG.info("Requesting authorization for Spotify connection...");
    requestState = UUID.randomUUID().toString();

    final String authenticationPromptUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/authorize/")
        .queryParam("client_id", spotifyAppId)
        .queryParam("response_type", "code")
        .queryParam("redirect_uri", redirectUri)
        .queryParam("scope", TOKEN_SCOPE)
        .queryParam("state", requestState)
        .build()
        .toString();

    LOG.info("Please go to the following address to grant authorization to spotnik:");
    LOG.info(authenticationPromptUrl);
    return authenticationPromptUrl;
  }

  void handleAuthorizationGranted(final String state, final String code) {
    if (state.equals(requestState)) {
      requestState = UUID.randomUUID().toString(); // Invalidate the request state
      requestAccessToken(code);
    } else {
      LOG.warn("Received request with invalid state!");
    }
  }

  private void requestAccessToken(final String code) {
    LOG.info("Requesting access token...");

    webClient.post()
        .uri(uriBuilder -> uriBuilder
            .queryParam("grant_type", "authorization_code")
            .queryParam("code", code)
            .queryParam("redirect_uri", redirectUri)
            .build())
        .exchange()
        .doOnNext(response -> {
          if (response.statusCode() == HttpStatus.OK) {
            LOG.info("RequestAccessToken httpStatus={}", response.statusCode());
          } else {
            LOG.warn("RequestAccessToken httpStatus={}", response.statusCode());
          }
        })
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(TokensDTO.class))
        .doOnNext(this::storeTokens)
        .doAfterTerminate(onAuthenticatedCallback)
        .subscribe();
  }

  private void storeTokens(final TokensDTO tokensDTO) {
    accessToken = tokensDTO.getAccessToken();
    tokensDTO.getRefreshToken().ifPresent(rt -> this.refreshToken = rt);

    final long tokenExpiresInSeconds = tokensDTO.getExpiresIn();
    accessTokenExpirationDate = LocalDateTime.now().plusSeconds(tokenExpiresInSeconds);

    LOG.info("Retrieved access tokens!");
  }

  void handleAuthorizationDenied(final String state) {
    if (state.equals(requestState)) {
      requestState = UUID.randomUUID().toString(); // Invalidate the request state
    } else {
      LOG.warn("Received request with invalid state!");
    }
  }

  boolean hasAccessToken() {
    return accessToken != null;
  }

  String getAccessToken() {
    refreshTokenIfNecessary();

    if (accessToken == null) {
      LOG.error("Getting access token, but it is null!");
    }

    return accessToken;
  }

  private void refreshTokenIfNecessary() {
    if (accessTokenExpirationDate == null) {
      LOG.warn("No accessTokenExpirationDate, trying to refresh!");
      refreshToken();
      return;
    }

    final LocalDateTime refreshDate = accessTokenExpirationDate.minusMinutes(REFRESH_THRESHOLD_MINUTES);
    if (LocalDateTime.now().isAfter(refreshDate)) {
      refreshToken();
    }
  }

  private void refreshToken() {
    LOG.info("Refreshing access token...");

    webClient.post()
        .uri(uriBuilder -> uriBuilder
            .queryParam("grant_type", "refresh_token")
            .queryParam("refresh_token", refreshToken)
            .build())
        .exchange()
        .doOnNext(response -> {
          if (response.statusCode() == HttpStatus.OK) {
            LOG.info("RefreshAccessToken httpStatus={}", response.statusCode());
          } else {
            LOG.warn("RefreshAccessToken httpStatus={}", response.statusCode());
          }
        })
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(TokensDTO.class))
        .subscribe(this::storeTokens);
  }

  void deleteTokens() {
    accessToken = null;
    refreshToken = null;
    LOG.warn("Access and refresh tokens have been deleted!");
  }

  public void setOnAuthenticatedCallback(final Runnable onAuthenticatedCallback) {
    this.onAuthenticatedCallback = onAuthenticatedCallback;
  }
}

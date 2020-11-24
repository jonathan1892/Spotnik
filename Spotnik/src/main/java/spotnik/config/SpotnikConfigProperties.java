package spotnik.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SpotnikConfigProperties.class)
@ConfigurationProperties(prefix = "spotnik")
public class SpotnikConfigProperties {

  private String spotifyAppId;
  private String spotifyAppSecret;
  private String redirectUri;
  private String redisHost;
  private String redisPort;
  private String adminUsername;
  private String adminPassword;

  public String getSpotifyAppId() {
    return spotifyAppId;
  }

  public void setSpotifyAppId(final String spotifyAppId) {
    this.spotifyAppId = spotifyAppId;
  }

  public String getSpotifyAppSecret() {
    return spotifyAppSecret;
  }

  public void setSpotifyAppSecret(final String spotifyAppSecret) {
    this.spotifyAppSecret = spotifyAppSecret;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(final String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public String getRedisHost() {
    return redisHost;
  }

  public void setRedisHost(final String redisHost) {
    this.redisHost = redisHost;
  }

  public String getRedisPort() {
    return redisPort;
  }

  public void setRedisPort(final String redisPort) {
    this.redisPort = redisPort;
  }

  public String getAdminUsername() {
    return adminUsername;
  }

  public void setAdminUsername(final String adminUsername) {
    this.adminUsername = adminUsername;
  }

  public String getAdminPassword() {
    return adminPassword;
  }

  public void setAdminPassword(final String adminPassword) {
    this.adminPassword = adminPassword;
  }
}


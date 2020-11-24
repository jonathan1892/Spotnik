package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokensDTO {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expires_in")
  private long expiresIn;

  @JsonProperty("refresh_token")
  private String refreshToken;

  public String getAccessToken() {
    return accessToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public Optional<String> getRefreshToken() {
    return Optional.ofNullable(refreshToken);
  }
}

package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageDTO {

  @JsonProperty
  private String url;

  public String getUrl() {
    return url;
  }
}

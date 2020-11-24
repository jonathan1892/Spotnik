package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDTO {

  @JsonProperty
  private String name;

  public String getName() {
    return name;
  }
}

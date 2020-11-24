package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaylistDTO {

  @JsonProperty
  private String name;

  public String getName() {
    return name;
  }
}

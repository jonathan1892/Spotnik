package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RecommendedTracksDTO {

  @JsonProperty
  private List<TrackDTO> tracks;

  public List<TrackDTO> getTracks() {
    return tracks;
  }
}

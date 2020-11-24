package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TracksDTO {

  @JsonProperty("items")
  private List<TrackDTO> trackItems;

  public List<TrackDTO> getTrackItems() {
    return trackItems;
  }
}

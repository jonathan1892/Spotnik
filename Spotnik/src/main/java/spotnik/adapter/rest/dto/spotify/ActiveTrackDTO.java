package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import spotnik.ActiveTrack;

public class ActiveTrackDTO {

  @JsonProperty("progress_ms")
  private Long progress;

  @JsonProperty("item")
  private TrackDTO track;

  public ActiveTrack toActiveSpotifyTrack() {
    return new ActiveTrack(Duration.ofMillis(progress), track.toSpotifyTrack());
  }
}

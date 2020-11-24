package spotnik.adapter.rest.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import spotnik.ActiveTrack;

public class ActiveSpotifyTrackDTO {

  @JsonProperty("track")
  private final SpotifyTrackDTO spotifyTrackDTO;

  @JsonProperty
  private final long timeLeft;

  private ActiveSpotifyTrackDTO(final SpotifyTrackDTO spotifyTrackDTO, final long timeLeft) {
    this.spotifyTrackDTO = spotifyTrackDTO;
    this.timeLeft = timeLeft;
  }

  public static ActiveSpotifyTrackDTO fromActiveSpotifyTrack(final ActiveTrack activeTrack) {
    return new ActiveSpotifyTrackDTO(
        SpotifyTrackDTO.fromSpotifyTrack(activeTrack.getTrack()),
        activeTrack.getTimeLeft().toSeconds()
    );
  }
}

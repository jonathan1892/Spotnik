package spotnik.adapter.rest.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import spotnik.PendingTrack;

public class PendingTrackDTO {

  @JsonProperty("track")
  private final SpotifyTrackDTO spotifyTrackDTO;

  @JsonProperty
  private final long timeBeforePlaying;

  private PendingTrackDTO(final SpotifyTrackDTO spotifyTrackDTO, final long timeBeforePlaying) {
    this.spotifyTrackDTO = spotifyTrackDTO;
    this.timeBeforePlaying = timeBeforePlaying;
  }

  public static PendingTrackDTO from(final PendingTrack pendingTrack) {
    return new PendingTrackDTO(
        SpotifyTrackDTO.fromSpotifyTrack(pendingTrack.getTrack()),
        pendingTrack.getTimeBeforePlaying().toSeconds()
    );
  }
}

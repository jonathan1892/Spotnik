package spotnik.adapter.rest.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import spotnik.PendingTrack;

public class PlaylistDTO {

  @JsonProperty
  private List<PendingTrackDTO> items;

  @JsonProperty
  private String name;

  public PlaylistDTO(final List<PendingTrackDTO> items, final String name) {
    this.items = items;
    this.name = name;
  }

  public static PlaylistDTO from(final List<PendingTrack> playlistTracks, final String playlistName) {
    return new PlaylistDTO(playlistTracks.stream().map(PendingTrackDTO::from).collect(Collectors.toList()), playlistName);
  }
}

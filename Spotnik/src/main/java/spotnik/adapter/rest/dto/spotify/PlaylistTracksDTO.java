package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PlaylistTracksDTO {

  @JsonProperty("items")
  private List<PlaylistTrackItemDTO> playlistTrackItemDTO;

  public List<PlaylistTrackItemDTO> getPlaylistTrackItemDTO() {
    return playlistTrackItemDTO;
  }
}

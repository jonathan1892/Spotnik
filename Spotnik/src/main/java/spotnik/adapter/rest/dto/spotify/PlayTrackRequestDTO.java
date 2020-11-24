package spotnik.adapter.rest.dto.spotify;

import java.util.List;

public class PlayTrackRequestDTO {

  public final List<String> uris;

  public PlayTrackRequestDTO(final String uri) {
    this.uris = List.of(uri);
  }
}

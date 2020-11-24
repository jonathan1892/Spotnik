package spotnik.adapter.rest.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import spotnik.Track;

public class SpotifyTrackDTO {

  @JsonProperty
  private final String uri;

  @JsonProperty
  private final String albumName;

  @JsonProperty
  private final String coverUrl;

  @JsonProperty
  private final String trackName;

  @JsonProperty
  private final String artists;

  @JsonProperty
  private final long duration;

  private SpotifyTrackDTO(final String uri, final String albumName, final String coverUrl, final String trackName, final String artists,
      final long duration) {
    this.uri = uri;
    this.albumName = albumName;
    this.coverUrl = coverUrl;
    this.trackName = trackName;
    this.artists = artists;
    this.duration = duration;
  }

  public static SpotifyTrackDTO fromSpotifyTrack(final Track track) {
    return new SpotifyTrackDTO(
        track.getUri(),
        track.getAlbumName(),
        track.getCoverUrl(),
        track.getTrackName(),
        track.getArtists(),
        track.getDuration().toSeconds()
    );
  }

}

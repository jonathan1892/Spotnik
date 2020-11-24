package spotnik.adapter.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import spotnik.Track;

public class TrackDTO {

  @JsonProperty
  private String uri;

  @JsonProperty
  private String albumName;

  @JsonProperty
  private String coverUrl;

  @JsonProperty
  private String trackName;

  @JsonProperty
  private String artists;

  @JsonProperty
  private long durationMilliseconds;

  public TrackDTO() {
  }

  private TrackDTO(final String uri, final String albumName, final String coverUrl, final String trackName, final String artists,
      final long durationMilliseconds) {
    this.uri = uri;
    this.albumName = albumName;
    this.coverUrl = coverUrl;
    this.trackName = trackName;
    this.artists = artists;
    this.durationMilliseconds = durationMilliseconds;
  }

  public static TrackDTO fromSpotifyTrack(final Track track) {
    return new TrackDTO(
        track.getUri(),
        track.getAlbumName(),
        track.getCoverUrl(),
        track.getTrackName(),
        track.getArtists(),
        track.getDuration().toMillis());
  }

  public Track toSpotifyTrack() {
    return new Track(
        uri,
        albumName,
        coverUrl,
        trackName,
        artists,
        Duration.ofMillis(durationMilliseconds));
  }
}

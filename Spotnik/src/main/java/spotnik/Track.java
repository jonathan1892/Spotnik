package spotnik;

import java.time.Duration;
import java.util.Objects;

public class Track {

  private final String uri;

  private final String albumName;
  private final String coverUrl;
  private final String trackName;
  private final String artists;
  private final Duration duration;

  public Track(final String uri, final String albumName, final String coverUrl, final String trackName, final String artists,
      final Duration duration) {
    this.uri = uri;
    this.albumName = albumName;
    this.coverUrl = coverUrl;
    this.trackName = trackName;
    this.artists = artists;
    this.duration = duration;
  }

  public String getUri() {
    return uri;
  }

  public String getAlbumName() {
    return albumName;
  }

  public String getCoverUrl() {
    return coverUrl;
  }

  public String getTrackName() {
    return trackName;
  }

  public String getArtists() {
    return artists;
  }

  public Duration getDuration() {
    return duration;
  }

  public String getId() {
    return uri.split(":")[2];
  }

  @Override
  public String toString() {
    return "Track{" +
        "uri='" + uri + '\'' +
        ", albumName='" + albumName + '\'' +
        ", trackName='" + trackName + '\'' +
        ", artists='" + artists + '\'' +
        ", duration=" + duration +
        '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Track that = (Track) o;
    return Objects.equals(uri, that.uri);
  }

  boolean isProbablyTheSameSong(final Track that) {
    if (trackName == null || that.trackName == null || artists == null || that.artists == null) {
      return false;
    }

    return Objects.equals(trackName, that.trackName)
        && Objects.equals(artists, that.artists);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri);
  }
}

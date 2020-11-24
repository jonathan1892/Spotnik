package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import spotnik.Track;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDTO {

  @JsonProperty
  private String uri;

  @JsonProperty("duration_ms")
  private long duration;

  @JsonProperty
  private AlbumDTO album;

  @JsonProperty("name")
  private String trackName;

  @JsonProperty
  private List<ArtistDTO> artists;

  @JsonProperty
  private boolean explicit;

  public long getDuration() {
    return duration;
  }

  public Track toSpotifyTrack() {
    final String artistsDescription = artists.stream()
        .map(ArtistDTO::getName)
        .collect(Collectors.joining(", "));

    final String constructedTrackName = trackName + (explicit ? " (explicit)" : "");
    final String imageUri = album.getImages().isEmpty() ? null : album.getImages().get(0).getUrl();
    return new Track(uri, album.getName(), imageUri, constructedTrackName, artistsDescription, Duration.ofMillis(duration));
  }
}

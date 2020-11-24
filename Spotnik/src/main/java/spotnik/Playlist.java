package spotnik;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Playlist {

  private static final Logger LOG = LoggerFactory.getLogger(Playlist.class);

  private final SpotifyApi spotifyApi;

  private String name;
  private List<Track> tracks;

  public Playlist(final SpotifyApi spotifyApi) {
    this.spotifyApi = spotifyApi;
    this.tracks = Collections.emptyList();
  }

  void initialize(final String playlistUri) {
    LOG.info("Queuing playlist with uri={}", playlistUri);
    tracks = spotifyApi.getTracksInPlaylist(playlistUri).collectList().block();
    name = spotifyApi.getPlaylistName(playlistUri).block();
    Collections.shuffle(tracks);
  }

  void clear() {
    name = null;
    tracks = Collections.emptyList();
  }

  Optional<Track> popFirst() {
    LOG.info("Retrieving first track from playlist");

    if (tracks.isEmpty()) {
      LOG.info("Playlist is empty");
      return Optional.empty();
    } else {
      final Track track = tracks.get(0);
      LOG.info("Retrieved track={}", track);
      tracks.remove(0);
      return Optional.of(track);
    }
  }

  List<Track> list() {
    return List.copyOf(tracks);
  }

  boolean isEmpty() {
    return tracks.isEmpty();
  }

  public String getName() {
    return name;
  }
}

package spotnik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class PlaylistTest {

  private Playlist playlist;
  private SpotifyApi spotifyApi;

  @BeforeEach
  void setUp() {
    spotifyApi = mock(SpotifyApi.class);
    playlist = new Playlist(spotifyApi);
  }

  @Test
  void popFirst() {
    final Track track1 = TestUtil.createTrackWithId("1345678");
    final Track track2 = TestUtil.createTrackWithId("2456781");
    when(spotifyApi.getTracksInPlaylist(anyString())).thenReturn(Flux.just(track1, track2));
    when(spotifyApi.getPlaylistName(anyString())).thenReturn(Mono.just("Playlist1"));

    playlist.initialize("");

    final List<Optional<Track>> poppedTracks = List.of(playlist.popFirst(), playlist.popFirst());
    assertThat(poppedTracks).containsExactlyInAnyOrder(Optional.of(track1), Optional.of(track2));
    assertThat(playlist.popFirst()).isEqualTo(Optional.empty());
  }

  @Test
  void list() {
    final Track track1 = TestUtil.createTrackWithId("1345678");
    final Track track2 = TestUtil.createTrackWithId("2456781");
    when(spotifyApi.getTracksInPlaylist(anyString())).thenReturn(Flux.just(track1, track2));
    when(spotifyApi.getPlaylistName(anyString())).thenReturn(Mono.just("Playlist1"));

    playlist.initialize("");

    assertThat(playlist.list()).containsExactlyInAnyOrder(track1, track2);
  }

  @Test
  void getName() {
    when(spotifyApi.getTracksInPlaylist(anyString())).thenReturn(Flux.empty());
    when(spotifyApi.getPlaylistName(anyString())).thenReturn(Mono.just("Playlist1"));

    playlist.initialize("");

    assertThat(playlist.getName()).isEqualTo("Playlist1");

  }
}
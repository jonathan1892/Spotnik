package spotnik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class TrackCacheTest {

  private TrackCache trackCache;
  private CachedTracksRepository cachedTracksRepository;
  private SpotifyApi spotifyApi;

  @BeforeEach
  void setUp() {
    cachedTracksRepository = mock(CachedTracksRepository.class);
    spotifyApi = mock(SpotifyApi.class);
    trackCache = new TrackCache(cachedTracksRepository, spotifyApi);
  }

  @Test
  void getTrackByUri_AlreadyInCache() {
    final Track track1 = TestUtil.createTrackWithId("123456");
    when(cachedTracksRepository.getTrackByUri(track1.getUri())).thenReturn(Optional.of(track1));

    assertThat(trackCache.getTrackByUri(track1.getUri())).isEqualTo(track1);
  }

  @Test
  void getTrackByUri_NotInCache() {
    final Track track1 = TestUtil.createTrackWithId("123456");
    when(cachedTracksRepository.getTrackByUri(track1.getUri())).thenReturn(Optional.empty());
    when(spotifyApi.findTrack(track1.getUri())).thenReturn(Mono.just(track1));

    assertThat(trackCache.getTrackByUri(track1.getUri())).isEqualTo(track1);
    verify(spotifyApi).findTrack(track1.getUri());
    verify(cachedTracksRepository).storeTrack(track1);
  }

  @Test
  void getTrackByUri_NotInCacheNotFound() {
    when(cachedTracksRepository.getTrackByUri(anyString())).thenReturn(Optional.empty());
    when(spotifyApi.findTrack(anyString())).thenReturn(Mono.empty());

    assertThatThrownBy(() -> trackCache.getTrackByUri("oops")).isInstanceOf(IllegalArgumentException.class);
    verify(spotifyApi).findTrack(anyString());
    verify(cachedTracksRepository, times(0)).storeTrack(any());
  }
}
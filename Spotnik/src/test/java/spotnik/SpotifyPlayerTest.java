package spotnik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class SpotifyPlayerTest {

  private SpotifyPlayer spotifyPlayer;
  private SpotifyApi spotifyApi;

  @BeforeEach
  void setUp() {
    spotifyApi = mock(SpotifyApi.class);
    spotifyPlayer = new SpotifyPlayer(spotifyApi);

    when(spotifyApi.playTrack(any())).thenReturn(Mono.just("").then());
  }

  @Test
  void playTrack() {
    final Track track = createTrackWithId("245654");
    spotifyPlayer.playTrack(track);

    assertThat(spotifyPlayer.isPlayerBusy()).isTrue();
    verify(spotifyApi).playTrack(track);
  }

  private Track createTrackWithId(final String id) {
    return new Track("spotify:track:" + id,
        "Album" + id,
        "https://image.png",
        "Track" + id,
        "artists" + id,
        Duration.ofSeconds(5));
  }

  @Test
  void autoplay() {
    final Track track = createTrackWithId("245654");
    spotifyPlayer.playTrack(track);

    final Track recommendedTrack = createTrackWithId("245624");
    when(spotifyApi.getRecommendedTrackBasedOn(track)).thenReturn(Mono.just(recommendedTrack));

    spotifyPlayer.autoplay();
    verify(spotifyApi).playTrack(recommendedTrack);
  }

  @Test
  void autoplay_DontPlaySameSongTwice() {
    final Track track = createTrackWithId("245654");
    spotifyPlayer.playTrack(track);

    when(spotifyApi.getRecommendedTrackBasedOn(track)).thenReturn(Mono.just(track));

    spotifyPlayer.autoplay();
    verify(spotifyApi).playTrack(any());
  }

  @Test
  void updatePlayerWithSpotifyState_CorrectSongIsNotCloseToEnding() {
    final Track trackToPlay = createTrackWithId("945967");
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(0), trackToPlay);
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.playTrack(trackToPlay);
    spotifyPlayer.updatePlayerWithSpotifyState();
    assertThat(spotifyPlayer.isPlayerBusy()).isTrue();
  }

  @Test
  void updatePlayerWithSpotifyState_CorrectSongIsCloseToEnding() {
    final Track trackToPlay = createTrackWithId("945967");
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(4), trackToPlay);
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.playTrack(trackToPlay);
    spotifyPlayer.updatePlayerWithSpotifyState();
    assertThat(spotifyPlayer.isPlayerBusy()).isFalse();
  }

  @Test
  void updatePlayerWithSpotifyState_WrongSongIsCloseToEnding() {
    final Track trackToPlay = createTrackWithId("945967");
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(4), createTrackWithId("965432"));
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.playTrack(trackToPlay);
    spotifyPlayer.updatePlayerWithSpotifyState();
    assertThat(spotifyPlayer.isPlayerBusy()).isTrue();
  }

  @Test
  void updatePlayerWithSpotifyState_NothingIsPlaying() {
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.empty());

    spotifyPlayer.updatePlayerWithSpotifyState();
    assertThat(spotifyPlayer.isPlayerBusy()).isFalse();
  }

  @Test
  void updatePlayerWithSpotifyState_NoTrackToPlay() {
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(4), createTrackWithId("965432"));
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.updatePlayerWithSpotifyState();
    assertThat(spotifyPlayer.isPlayerBusy()).isFalse();
  }

  @Test
  void enforceTrackToPlayIsActive_CorrectSongIsPlaying() {
    final Track trackToPlay = createTrackWithId("945967");
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(0), trackToPlay);
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.playTrack(trackToPlay);
    spotifyPlayer.updatePlayerWithSpotifyState();
    spotifyPlayer.enforceTrackToPlayIsActive();
    verify(spotifyApi, times(1)).playTrack(trackToPlay);
  }

  @Test
  void enforceTrackToPlayIsActive_WrongSongIsPlaying() {
    final Track trackToPlay = createTrackWithId("945967");
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(0), createTrackWithId("965432"));
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.playTrack(trackToPlay);
    spotifyPlayer.updatePlayerWithSpotifyState();
    spotifyPlayer.enforceTrackToPlayIsActive();
    verify(spotifyApi, times(2)).playTrack(trackToPlay);
  }

  @Test
  void enforceTrackToPlayIsActive_NothingIsPlaying() {
    final Track trackToPlay = createTrackWithId("945967");
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.empty());

    spotifyPlayer.playTrack(trackToPlay);
    spotifyPlayer.updatePlayerWithSpotifyState();
    spotifyPlayer.enforceTrackToPlayIsActive();
    verify(spotifyApi, times(2)).playTrack(trackToPlay);
  }

  @Test
  void enforceTrackToPlayIsActive_NoTrackToPlay() {
    final ActiveTrack activeTrack = new ActiveTrack(Duration.ofSeconds(0), createTrackWithId("965432"));
    when(spotifyApi.getActiveTrack()).thenReturn(Mono.just(activeTrack));

    spotifyPlayer.updatePlayerWithSpotifyState();
    spotifyPlayer.enforceTrackToPlayIsActive();
    verify(spotifyApi, times(0)).playTrack(any());
  }
}
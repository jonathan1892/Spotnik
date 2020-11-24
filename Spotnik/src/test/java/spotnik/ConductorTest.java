package spotnik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ConductorTest {

  private Conductor conductor;
  private TrackQueue trackQueue;
  private Playlist playlist;
  private SpotifyPlayer spotifyPlayer;

  @BeforeEach
  void setUp() {
    spotifyPlayer = mock(SpotifyPlayer.class);
    trackQueue = mock(TrackQueue.class);
    playlist = mock(Playlist.class);
    conductor = new Conductor(spotifyPlayer, trackQueue, playlist);
  }

  @Test
  void playNextTrack() {
    final Track track1 = TestUtil.createTrackWithId("1345678");
    final Track track2 = TestUtil.createTrackWithId("1342678");

    when(spotifyPlayer.isPlayerBusy()).thenReturn(false);
    when(trackQueue.popFirst()).thenReturn(Optional.of(track1), Optional.of(track2));

    conductor.playNextTrackWhenPlayerIsAvailable();
    conductor.playNextTrackWhenPlayerIsAvailable();

    verify(spotifyPlayer).playTrack(track1);
    verify(spotifyPlayer).playTrack(track2);
    verify(trackQueue, times(2)).popFirst();
  }

  @Test
  void forcePlayNextQueuedTrack() {
    when(spotifyPlayer.isPlayerBusy()).thenReturn(true);

    final Track track1 = TestUtil.createTrackWithId("1345678");
    when(trackQueue.popFirst()).thenReturn(Optional.of(track1));

    conductor.forcePlay();

    verify(spotifyPlayer).playTrack(track1);
    verify(trackQueue).popFirst();
  }

  @Test
  void playNextTrack_QueueIsEmptyAndThereIsAFallbackPlaylist() {
    when(spotifyPlayer.isPlayerBusy()).thenReturn(false);
    when(trackQueue.popFirst()).thenReturn(Optional.empty());
    final Track track1 = TestUtil.createTrackWithId("1345678");
    when(playlist.popFirst()).thenReturn(Optional.of(track1));

    conductor.playNextTrackWhenPlayerIsAvailable();

    verify(spotifyPlayer).playTrack(track1);
  }

  @Test
  void playNextTrack_QueueIsEmptyNoFallbackPlaylist() {
    when(spotifyPlayer.isPlayerBusy()).thenReturn(false);
    when(trackQueue.popFirst()).thenReturn(Optional.empty());
    when(playlist.popFirst()).thenReturn(Optional.empty());

    conductor.playNextTrackWhenPlayerIsAvailable();

    verify(spotifyPlayer).autoplay();
  }

  @Test
  void playNextTrack_WaitsForPlayerToBeAvailable() {
    when(spotifyPlayer.isPlayerBusy()).thenReturn(true, false);

    final Track track1 = TestUtil.createTrackWithId("1345678");
    when(trackQueue.popFirst()).thenReturn(Optional.of(track1));

    conductor.playNextTrackWhenPlayerIsAvailable();

    final InOrder inOrder = inOrder(spotifyPlayer);
    inOrder.verify(spotifyPlayer, times(2)).isPlayerBusy();
    inOrder.verify(spotifyPlayer).playTrack(track1);
  }

  @Test
  void queueTrack() {
    conductor.queueTrack("1345678");
    conductor.queueTrack("2456781");

    verify(trackQueue).queueTrack("1345678");
    verify(trackQueue).queueTrack("2456781");
  }

  @Test
  void queuePlaylist() {
    when(playlist.isEmpty()).thenReturn(true);
    conductor.setFallbackPlaylist("1244323");

    verify(playlist).initialize("1244323");
  }

  @Test
  void describeQueue() {
    final Track track1 = new Track("track345678", "Album", "url", "Name 1", "Artist 1", Duration.ofSeconds(120));
    final Track track2 = new Track("track345446", "Album", "url", "Name 2", "Artist 2", Duration.ofSeconds(300));
    when(trackQueue.list()).thenReturn(List.of(track1, track2));
    final Track track3 = new Track("track345670", "Album", "url", "Name 3", "Artist 3", Duration.ofSeconds(220));
    when(playlist.list()).thenReturn(List.of(track3));
    when(playlist.getName()).thenReturn("PlaylistName");
    when(spotifyPlayer.getActiveTrack()).thenReturn(Optional.of(new ActiveTrack(Duration.ofSeconds(10),
        new Track("track345478", "Album", "url", "Name 0", "Artist 0", Duration.ofSeconds(100)))));

    final QueueDescription queueDescription = conductor.describe();
    final PendingTrack pendingTrack1 = new PendingTrack(track1, Duration.ofSeconds(90));
    final PendingTrack pendingTrack2 = new PendingTrack(track2, Duration.ofSeconds(210));
    assertThat(queueDescription.getQueuedTracks()).containsExactly(pendingTrack1, pendingTrack2);

    final PendingTrack pendingTrack3 = new PendingTrack(track3, Duration.ofSeconds(510));
    assertThat(queueDescription.getPlaylistTracks()).containsExactly(pendingTrack3);

    assertThat(queueDescription.getPlaylistName()).isEqualTo("PlaylistName");
  }
}
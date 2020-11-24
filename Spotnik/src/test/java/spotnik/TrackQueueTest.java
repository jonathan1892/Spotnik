package spotnik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackQueueTest {

  private TrackQueue trackQueue;
  private QueuedTracksRepository queuedTracksRepository;
  private TrackCache trackCache;

  @BeforeEach
  void setUp() {
    queuedTracksRepository = mock(QueuedTracksRepository.class);
    trackCache = mock(TrackCache.class);
    trackQueue = new TrackQueue(queuedTracksRepository, trackCache);
  }

  @Test
  void queueTrack() {
    final Track track1 = TestUtil.createTrackWithId("1345678");
    final Track track2 = TestUtil.createTrackWithId("2456781");
    when(trackCache.getTrackByUri(track1.getUri())).thenReturn(track1);
    when(trackCache.getTrackByUri(track2.getUri())).thenReturn(track2);
    when(queuedTracksRepository.list()).thenReturn(List.of(), List.of(track1));

    trackQueue.queueTrack(track1.getUri());
    trackQueue.queueTrack(track2.getUri());

    verify(queuedTracksRepository).append(track1);
    verify(queuedTracksRepository).append(track2);
  }

  @Test
  void queueTrack_SkipSimilarSongs() {
    final Track track1 = new Track("1345678", "Album1", "url", "Track1", "Artist A", Duration.ZERO);
    final Track track2 = new Track("1345658", "Album2", "url", "Track2", "Artist B", Duration.ZERO);
    final Track track3 = new Track("1343678", "Album3", "url", "Track2", "Artist B", Duration.ZERO);

    when(trackCache.getTrackByUri(track1.getUri())).thenReturn(track1);
    when(trackCache.getTrackByUri(track2.getUri())).thenReturn(track2);
    when(trackCache.getTrackByUri(track3.getUri())).thenReturn(track3);
    when(queuedTracksRepository.list()).thenReturn(List.of(), List.of(track1), List.of(track1, track2));

    trackQueue.queueTrack(track1.getUri());
    trackQueue.queueTrack(track2.getUri());
    trackQueue.queueTrack(track3.getUri());

    verify(queuedTracksRepository).append(track1);
    verify(queuedTracksRepository).append(track2);
    verify(queuedTracksRepository, times(0)).append(track3);
  }

  @Test
  void describeQueue() {
    final Track track1 = new Track("track345678", "Album", "url", "Name 1", "Artist 1", Duration.ofSeconds(120));
    final Track track2 = new Track("track345446", "Album", "url", "Name 2", "Artist 2", Duration.ofSeconds(300));

    when(queuedTracksRepository.list()).thenReturn(List.of(track1, track2));

    assertThat(trackQueue.list()).containsExactly(track1, track2);
  }

  @Test
  void popTrack() {
    when(queuedTracksRepository.isEmpty()).thenReturn(false);
    final Track track1 = new Track("track345678", "Album", "url", "Name 1", "Artist 1", Duration.ofSeconds(120));
    when(queuedTracksRepository.popFirst()).thenReturn(track1);

    assertThat(trackQueue.popFirst()).isEqualTo(Optional.of(track1));
  }

  @Test
  void popTrack_WhenQueueEmpty() {
    when(queuedTracksRepository.isEmpty()).thenReturn(true);
    assertThat(trackQueue.popFirst()).isEqualTo(Optional.empty());
  }
}
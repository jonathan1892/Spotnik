package spotnik;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conductor {

  private static final Logger LOG = LoggerFactory.getLogger(Conductor.class);

  private final SpotifyPlayer spotifyPlayer;
  private final TrackQueue trackQueue;
  private final Playlist fallbackPlaylist;

  private Thread monitorQueueThread;

  public Conductor(final SpotifyPlayer spotifyPlayer, final TrackQueue trackQueue, final Playlist fallbackPlaylist) {
    this.spotifyPlayer = spotifyPlayer;
    this.trackQueue = trackQueue;
    this.fallbackPlaylist = fallbackPlaylist;
  }

  public void queueTrack(final String uri) {
    trackQueue.queueTrack(uri);
  }

  public void setFallbackPlaylist(final String uri) {
    if (fallbackPlaylist.isEmpty()) {
      fallbackPlaylist.initialize(uri);
    } else {
      throw new IllegalStateException("A fallback playlist is already set!");
    }
  }

  public void clearFallbackPlaylist() {
    fallbackPlaylist.clear();
  }

  public QueueDescription describe() {
    Duration timeBeforePlaying = spotifyPlayer.getActiveTrack().map(ActiveTrack::getTimeLeft).orElse(Duration.ZERO);

    final ArrayList<PendingTrack> queuedTracks = new ArrayList<>();
    for (final Track track : trackQueue.list()) {
      queuedTracks.add(new PendingTrack(track, timeBeforePlaying));
      timeBeforePlaying = timeBeforePlaying.plus(track.getDuration());
    }

    final ArrayList<PendingTrack> playlistTracks = new ArrayList<>();
    for (final Track track : fallbackPlaylist.list()) {
      playlistTracks.add(new PendingTrack(track, timeBeforePlaying));
      timeBeforePlaying = timeBeforePlaying.plus(track.getDuration());
    }
    return new QueueDescription(queuedTracks, playlistTracks, fallbackPlaylist.getName());
  }

  void startToConduct() {
    if (monitorQueueThread == null) {
      LOG.info("Starting thread to monitor trackQueue...");
      monitorQueueThread = new Thread(this::keepPlayingNextQueuedTrack);
      monitorQueueThread.setDaemon(true);
      monitorQueueThread.start();
      LOG.info("TrackQueue monitoring thread started.");
    } else {
      throw new RuntimeException("Trying to start a trackQueue monitoring thread while it is already running!");
    }
  }

  private void keepPlayingNextQueuedTrack() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        playNextTrackWhenPlayerIsAvailable();
        Util.sleep(Duration.ofMillis(500L));
      } catch (final Exception e) {
        LOG.warn(e.toString());
        LOG.error(e.toString());
      }
    }
    LOG.info("Exiting keepPlayingNextQueuedTrack");
  }

  void playNextTrackWhenPlayerIsAvailable() {
    waitForPlayerToBeAvailable();
    playNextTrack();
  }

  private void waitForPlayerToBeAvailable() {
    while (spotifyPlayer.isPlayerBusy()) {
      Util.sleep(Duration.ofMillis(200L));
    }
  }

  private void playNextTrack() {
    final Optional<Track> queuedTrack = trackQueue.popFirst();

    if (queuedTrack.isPresent()) {
      spotifyPlayer.playTrack(queuedTrack.get());
    } else {
      pickTrackForEmptyQueue();
    }
  }

  private void pickTrackForEmptyQueue() {
    final Optional<Track> playlistTrack = fallbackPlaylist.popFirst();

    if (playlistTrack.isPresent()) {
      spotifyPlayer.playTrack(playlistTrack.get());
    } else {
      spotifyPlayer.autoplay();
    }
  }

  public void forcePlay() {
    playNextTrack();
  }

  @PreDestroy
  public void destroy() {
    LOG.warn("Closing down Conductor");
    monitorQueueThread.interrupt();
  }
}

package spotnik;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpotifyPlayer {

  private static final Logger LOG = LoggerFactory.getLogger(SpotifyPlayer.class);

  private final SpotifyApi spotifyApi;

  private volatile Track trackToPlay;
  private volatile ActiveTrack activeTrack;
  private volatile Track lastPlayedTrack;

  private final ReentrantLock playTrackLock = new ReentrantLock();
  private Thread enforceCorrectTrackThread;
  private Thread monitorActiveTrackThread;

  public SpotifyPlayer(final SpotifyApi spotifyApi) {
    this.spotifyApi = spotifyApi;
  }

  void initPlayerWithExistingState() {
    spotifyApi.getActiveTrack()
        .log("Initializing Spotify player with current Spotify state")
        .subscribe(activeSpotifyTrack -> trackToPlay = activeSpotifyTrack.getTrack());
  }

  void playTrack(final Track track) {
    try {
      LOG.info("Starting to play track={}", track);
      this.trackToPlay = track;
      this.lastPlayedTrack = track;

      playTrackLock.lock();
      spotifyApi.playTrack(track).block();
    } finally {
      playTrackLock.unlock();
    }
  }

  void autoplay() {
    if (lastPlayedTrack != null) {
      LOG.info("Autoplaying next track...");
      final Track recommendedTrack = spotifyApi.getRecommendedTrackBasedOn(lastPlayedTrack)
          .filter(t -> !t.equals(lastPlayedTrack))
          .block();

      if (recommendedTrack != null) {
        playTrack(recommendedTrack);
      }
    }
  }

  boolean isPlayerBusy() {
    return trackToPlay != null;
  }

  void startDaemonThreads() {
    startToUpdatePlayerState();
    startToEnforceCorrectTrack();
  }

  private void startToUpdatePlayerState() {
    if (monitorActiveTrackThread == null) {
      LOG.info("Starting thread to monitor currently playing...");
      monitorActiveTrackThread = new Thread(this::keepUpdatingPlayerState);
      monitorActiveTrackThread.setDaemon(true);
      monitorActiveTrackThread.start();
      LOG.info("Currently playing monitoring thread started.");
    } else {
      throw new RuntimeException("Trying to start an already running Thread, thread=" + enforceCorrectTrackThread);
    }
  }

  private void keepUpdatingPlayerState() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        updatePlayerWithSpotifyState();
        Util.sleep(Duration.ofSeconds(1));
      } catch (final Exception e) {
        LOG.warn(e.toString());
        LOG.error(e.toString());
      }
    }
    LOG.info("Exiting keepUpdatingPlayerState");
  }

  void updatePlayerWithSpotifyState() {
    this.activeTrack = spotifyApi.getActiveTrack().block();
    LOG.info("activeTrack={}", this.activeTrack);

    if (hasTrackToPlayFinishedPlaying()) {
      LOG.info("Finished playing track={}", trackToPlay);
      trackToPlay = null;
    }
  }

  private boolean hasTrackToPlayFinishedPlaying() {
    return activeTrack != null
        && activeTrack.getTrack().equals(trackToPlay)
        && activeTrack.getTimeLeft().compareTo(Duration.ofMillis(1500)) < 0;
  }

  private void startToEnforceCorrectTrack() {
    if (enforceCorrectTrackThread == null) {
      LOG.info("Starting thread to enforce correct track...");
      enforceCorrectTrackThread = new Thread(this::keepEnforcingTrackToPlayIsActive);
      enforceCorrectTrackThread.setDaemon(true);
      enforceCorrectTrackThread.start();
      LOG.info("Enforce correct track thread started.");
    } else {
      throw new RuntimeException("Trying to start an already running Thread, thread=" + enforceCorrectTrackThread);
    }
  }

  private void keepEnforcingTrackToPlayIsActive() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        enforceTrackToPlayIsActive();
        Util.sleep(Duration.ofSeconds(10));
      } catch (final Exception e) {
        LOG.warn(e.toString());
        LOG.error(e.toString());
      }
    }
    LOG.info("Exiting keepEnforcingTrackToPlayIsActive");
  }

  void enforceTrackToPlayIsActive() {
    if (!isTrackToPlayActive()) {
      forcePlayActiveTrack();
    }
  }

  private boolean isTrackToPlayActive() {
    if (trackToPlay == null) {
      LOG.info("There is no track to play!");
      return true;
    }

    if (activeTrack == null || !activeTrack.getTrack().equals(trackToPlay)) {
      LOG.warn("Spotify is not playing trackToPlay={}, activeTrack={}", trackToPlay, activeTrack);
      return false;
    }

    LOG.info("The track to play is currently active (track={})", trackToPlay);
    return true;
  }

  private void forcePlayActiveTrack() {
    try {
      if (playTrackLock.tryLock()) {
        LOG.warn("Force playing track={}", trackToPlay);
        spotifyApi.playTrack(trackToPlay)
            .timeout(Duration.ofSeconds(5))
            .block();
      } else {
        LOG.warn("Unable to force play track={}, another song is about to play", trackToPlay);
      }
    } finally {
      if (playTrackLock.isHeldByCurrentThread()) {
        playTrackLock.unlock();
      }
    }
  }

  public Optional<ActiveTrack> getActiveTrack() {
    return Optional.ofNullable(activeTrack);
  }

  @PreDestroy
  public void destroy() {
    LOG.warn("Closing down SpotifyPlayer");
    enforceCorrectTrackThread.interrupt();
    monitorActiveTrackThread.interrupt();
  }
}

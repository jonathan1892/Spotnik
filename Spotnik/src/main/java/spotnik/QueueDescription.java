package spotnik;

import java.util.List;

public class QueueDescription {

  private final List<PendingTrack> queuedTracks;
  private final List<PendingTrack> playlistTracks;
  private final String playlistName;

  public QueueDescription(final List<PendingTrack> queuedTracks, final List<PendingTrack> playlistTracks, final String playlistName) {
    this.queuedTracks = queuedTracks;
    this.playlistTracks = playlistTracks;
    this.playlistName = playlistName;
  }

  public List<PendingTrack> getQueuedTracks() {
    return queuedTracks;
  }

  public List<PendingTrack> getPlaylistTracks() {
    return playlistTracks;
  }

  public String getPlaylistName() {
    return playlistName;
  }
}

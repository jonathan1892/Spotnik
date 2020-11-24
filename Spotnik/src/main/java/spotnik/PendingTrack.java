package spotnik;

import java.time.Duration;
import java.util.Objects;

public class PendingTrack {

  private final Track track;
  private final Duration timeBeforePlaying;

  public PendingTrack(final Track track, final Duration timeBeforePlaying) {
    this.track = track;
    this.timeBeforePlaying = timeBeforePlaying;
  }

  public Track getTrack() {
    return track;
  }

  public Duration getTimeBeforePlaying() {
    return timeBeforePlaying;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final PendingTrack that = (PendingTrack) o;
    return Objects.equals(track, that.track) &&
        Objects.equals(timeBeforePlaying, that.timeBeforePlaying);
  }

  @Override
  public int hashCode() {
    return Objects.hash(track, timeBeforePlaying);
  }

  @Override
  public String toString() {
    return "PendingTrack{" +
        "track=" + track +
        ", timeBeforePlaying=" + timeBeforePlaying +
        '}';
  }
}

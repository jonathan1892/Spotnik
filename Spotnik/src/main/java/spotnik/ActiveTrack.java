package spotnik;

import java.time.Duration;

public class ActiveTrack {

  private final Duration timeLeft;
  private final Track track;

  public ActiveTrack(final Duration progress, final Track track) {
    timeLeft = track.getDuration().minus(progress);
    this.track = track;
  }

  public Track getTrack() {
    return track;
  }

  public Duration getTimeLeft() {
    return timeLeft;
  }

  @Override
  public String toString() {
    return "ActiveTrack{" +
        "timeLeft=" + timeLeft +
        ", track=" + track +
        '}';
  }
}
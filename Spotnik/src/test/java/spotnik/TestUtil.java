package spotnik;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spotnik.adapter.redis.TrackDTO;

public final class TestUtil {

  private static final Logger LOG = LoggerFactory.getLogger(TestUtil.class);


  private TestUtil() {
  }

  public static Track createTrackWithId(final String id) {
    return new Track("spotify:track:" + id,
        "Album" + id,
        "https://image.png",
        "Track" + id,
        "artists" + id,
        Duration.ofSeconds(5));
  }

  public static String serializeSpotifyTrack(final Track track) {
    try {
      return new ObjectMapper().writeValueAsString(TrackDTO.fromSpotifyTrack(track));
    } catch (final JsonProcessingException e) {
      LOG.error(e.toString());
      throw new RuntimeException(e);
    }
  }

}

package spotnik.adapter.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import spotnik.QueuedTracksRepository;
import spotnik.Track;
import spotnik.adapter.rest.SpotifyApiImpl;

public class QueuedTracksRepositoryImpl implements QueuedTracksRepository {

  static final String QUEUE_KEY = "tracks:queue";
  private static final Logger LOG = LoggerFactory.getLogger(SpotifyApiImpl.class);

  private final JedisPool pool;
  private final ObjectMapper objectMapper;

  public QueuedTracksRepositoryImpl(final JedisPool jedisPool, final ObjectMapper objectMapper) {
    this.pool = jedisPool;
    this.objectMapper = objectMapper;
  }

  @Override
  public void append(final Track track) {
    LOG.info("Appending track={} to queue", track);

    try (final Jedis jedis = pool.getResource()) {
      jedis.rpush(QUEUE_KEY, serializeSpotifyTrack(track));
    }
  }

  private String serializeSpotifyTrack(final Track track) {
    try {
      return objectMapper.writeValueAsString(TrackDTO.fromSpotifyTrack(track));
    } catch (final JsonProcessingException e) {
      LOG.error(e.toString());
      throw new RuntimeException(e);
    }
  }

  @Override
  public Track popFirst() {
    final String serializedTrack;

    try (final Jedis jedis = pool.getResource()) {
      serializedTrack = jedis.lpop(QUEUE_KEY);
    }

    return deserializeSpotifyTrack(serializedTrack);
  }

  private Track deserializeSpotifyTrack(final String serializedTrack) {
    try {
      final TrackDTO trackDTO = objectMapper.readValue(serializedTrack, TrackDTO.class);
      return trackDTO.toSpotifyTrack();
    } catch (final IOException e) {
      LOG.error(e.toString());
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Track> list() {
    LOG.info("Fetching queue");
    final List<String> serializedTracks;

    try (final Jedis jedis = pool.getResource()) {
      serializedTracks = jedis.lrange(QUEUE_KEY, 0, -1);
    }

    return serializedTracks.stream()
        .map(this::deserializeSpotifyTrack)
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean isEmpty() {
    try (final Jedis jedis = pool.getResource()) {
      return jedis.llen(QUEUE_KEY) == 0;
    }
  }
}

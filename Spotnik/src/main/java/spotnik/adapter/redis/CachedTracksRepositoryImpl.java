package spotnik.adapter.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import spotnik.CachedTracksRepository;
import spotnik.Track;
import spotnik.adapter.rest.SpotifyApiImpl;

public class CachedTracksRepositoryImpl implements CachedTracksRepository {

  private static final Logger LOG = LoggerFactory.getLogger(SpotifyApiImpl.class);

  static final String CACHE_KEY = "tracks:cache";

  private final JedisPool pool;
  private final ObjectMapper objectMapper;

  public CachedTracksRepositoryImpl(final JedisPool jedisPool, final ObjectMapper objectMapper) {
    this.pool = jedisPool;
    this.objectMapper = objectMapper;
  }

  public void storeTrack(final Track track) {
    LOG.info("Storing track={}", track);

    final String serializedTrack = serializeSpotifyTrack(track);
    try (final Jedis jedis = pool.getResource()) {
      jedis.hset(CACHE_KEY, track.getUri(), serializedTrack);
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

  public Optional<Track> getTrackByUri(final String uri) {
    LOG.info("Looking for stored track with uri={}", uri);

    final String serializedTrack;
    try (final Jedis jedis = pool.getResource()) {
      serializedTrack = jedis.hget(CACHE_KEY, uri);
    }

    return deserializeSpotifyTrack(serializedTrack);
  }

  private Optional<Track> deserializeSpotifyTrack(final String serializedTrack) {
    if (serializedTrack == null) {
      return Optional.empty();
    }

    try {
      final TrackDTO trackDTO = objectMapper.readValue(serializedTrack, TrackDTO.class);
      return Optional.of(trackDTO.toSpotifyTrack());
    } catch (final IOException e) {
      LOG.error(e.toString());
      return Optional.empty();
    }
  }
}

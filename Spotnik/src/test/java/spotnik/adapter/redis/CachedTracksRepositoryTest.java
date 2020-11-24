package spotnik.adapter.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;
import spotnik.CachedTracksRepository;
import spotnik.TestUtil;
import spotnik.Track;

class CachedTracksRepositoryTest {

  private RedisServer redisServer;
  private ObjectMapper objectMapper;
  private CachedTracksRepository cachedTracksRepository;
  private Jedis jedis;

  @BeforeEach
  void setUp() throws IOException {
    this.redisServer = new redis.embedded.RedisServer(6379);
    this.objectMapper = new ObjectMapper();
    final JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
    cachedTracksRepository = new CachedTracksRepositoryImpl(jedisPool, objectMapper);

    redisServer.start();
    jedis = jedisPool.getResource();
  }

  @Test
  void storeTrack() {
    final Track track1 = TestUtil.createTrackWithId("123456789");

    cachedTracksRepository.storeTrack(track1);

    assertThat(jedis.hget(CachedTracksRepositoryImpl.CACHE_KEY, track1.getUri()))
        .isEqualTo(TestUtil.serializeSpotifyTrack(track1));
  }

  @Test
  void getTrackByUri() {
    final Track track1 = TestUtil.createTrackWithId("123456789");

    jedis.hset(CachedTracksRepositoryImpl.CACHE_KEY, track1.getUri(), TestUtil.serializeSpotifyTrack(track1));

    assertThat(cachedTracksRepository.getTrackByUri(track1.getUri())).isEqualTo(Optional.of(track1));
  }

  @AfterEach
  void tearDown() {
    jedis.close();
    this.redisServer.stop();
  }
}
package spotnik.adapter.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;
import spotnik.QueuedTracksRepository;
import spotnik.TestUtil;
import spotnik.Track;

class QueuedTracksRepositoryTest {

  private RedisServer redisServer;
  private ObjectMapper objectMapper;
  private QueuedTracksRepository queuedTracksRepository;
  private Jedis jedis;

  @BeforeEach
  void setUp() throws IOException {
    this.redisServer = new redis.embedded.RedisServer(6379);
    this.objectMapper = new ObjectMapper();
    final JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
    queuedTracksRepository = new QueuedTracksRepositoryImpl(jedisPool, objectMapper);

    redisServer.start();
    jedis = jedisPool.getResource();
  }

  @Test
  void append() {
    final Track track1 = TestUtil.createTrackWithId("123456789");
    final Track track2 = TestUtil.createTrackWithId("987654321");

    queuedTracksRepository.append(track1);
    queuedTracksRepository.append(track2);

    final String serializedTrack1 = TestUtil.serializeSpotifyTrack(track1);
    final String serializedTrack2 = TestUtil.serializeSpotifyTrack(track2);
    assertThat(jedis.lrange(QueuedTracksRepositoryImpl.QUEUE_KEY, 0, -1)).containsExactly(serializedTrack1, serializedTrack2);
  }

  @Test
  void popFirst() {
    final Track track1 = TestUtil.createTrackWithId("123456789");
    final Track track2 = TestUtil.createTrackWithId("987654321");
    final Track track3 = TestUtil.createTrackWithId("987654789");

    final String serializedTrack1 = TestUtil.serializeSpotifyTrack(track1);
    final String serializedTrack2 = TestUtil.serializeSpotifyTrack(track2);
    final String serializedTrack3 = TestUtil.serializeSpotifyTrack(track3);

    jedis.rpush(QueuedTracksRepositoryImpl.QUEUE_KEY, serializedTrack1);
    jedis.rpush(QueuedTracksRepositoryImpl.QUEUE_KEY, serializedTrack2);
    jedis.rpush(QueuedTracksRepositoryImpl.QUEUE_KEY, serializedTrack3);

    assertThat(queuedTracksRepository.popFirst()).isEqualTo(track1);
    assertThat(queuedTracksRepository.popFirst()).isEqualTo(track2);
    assertThat(jedis.lrange(QueuedTracksRepositoryImpl.QUEUE_KEY, 0, -1)).containsExactly(serializedTrack3);
  }

  @Test
  void list() {
    final Track track1 = TestUtil.createTrackWithId("123456789");
    final Track track2 = TestUtil.createTrackWithId("987654321");

    final String serializedTrack1 = TestUtil.serializeSpotifyTrack(track1);
    final String serializedTrack2 = TestUtil.serializeSpotifyTrack(track2);

    jedis.rpush(QueuedTracksRepositoryImpl.QUEUE_KEY, serializedTrack1);
    jedis.rpush(QueuedTracksRepositoryImpl.QUEUE_KEY, serializedTrack2);

    assertThat(queuedTracksRepository.list()).containsExactly(track1, track2);
  }

  @Test
  void isEmpty() {
    assertThat(queuedTracksRepository.isEmpty()).isTrue();

    final Track track1 = TestUtil.createTrackWithId("123456789");
    final String serializedTrack1 = TestUtil.serializeSpotifyTrack(track1);
    jedis.rpush(QueuedTracksRepositoryImpl.QUEUE_KEY, serializedTrack1);

    assertThat(queuedTracksRepository.isEmpty()).isFalse();
  }

  @AfterEach
  void tearDown() {
    jedis.close();
    this.redisServer.stop();
  }
}
package spotnik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import spotnik.CachedTracksRepository;
import spotnik.Conductor;
import spotnik.Playlist;
import spotnik.QueuedTracksRepository;
import spotnik.SpotifyApi;
import spotnik.SpotifyPlayer;
import spotnik.TrackCache;
import spotnik.TrackQueue;
import spotnik.adapter.redis.CachedTracksRepositoryImpl;
import spotnik.adapter.redis.QueuedTracksRepositoryImpl;
import spotnik.adapter.rest.SpotifyApiImpl;
import spotnik.adapter.rest.SpotifyCredentials;

@Configuration
public class SpotnikConfiguration {

  @Bean
  public TrackQueue createTrackQueue(final QueuedTracksRepository queuedTracksRepository, final TrackCache trackCache) {
    return new TrackQueue(queuedTracksRepository, trackCache);
  }

  @Bean
  public Playlist createPlaylistQueue(final SpotifyApi spotifyApi) {
    return new Playlist(spotifyApi);
  }

  @Bean
  public Conductor createConductor(final SpotifyPlayer spotifyPlayer, final TrackQueue trackQueue, final Playlist playlist) {
    return new Conductor(spotifyPlayer, trackQueue, playlist);
  }

  @Bean
  public SpotifyPlayer createSpotifyPlayer(final SpotifyApi spotifyApi) {
    return new SpotifyPlayer(spotifyApi);
  }

  @Bean
  public SpotifyApi createSpotifyApi(final SpotifyCredentials spotifyCredentials) {
    return new SpotifyApiImpl(spotifyCredentials, WebClient.builder());
  }

  @Bean
  public SpotifyCredentials createSpotifyCredentials(final SpotnikConfigProperties spotnikConfigProperties) {
    return new SpotifyCredentials(
        WebClient.builder(),
        spotnikConfigProperties.getSpotifyAppId(),
        spotnikConfigProperties.getSpotifyAppSecret(),
        spotnikConfigProperties.getRedirectUri()
    );
  }

  @Bean
  public QueuedTracksRepository createQueuedTracksRepository(final JedisPool jedisPool, final ObjectMapper objectMapper) {
    return new QueuedTracksRepositoryImpl(jedisPool, objectMapper);
  }

  @Bean
  public JedisPool createJedisPool(final SpotnikConfigProperties spotnikConfigProperties) {
    return new JedisPool(new JedisPoolConfig(), spotnikConfigProperties.getRedisHost(),
        Integer.parseInt(spotnikConfigProperties.getRedisPort()));
  }

  @Bean
  public TrackCache createTrackCache(final CachedTracksRepository cachedTracksRepository, final SpotifyApi spotifyApi) {
    return new TrackCache(cachedTracksRepository, spotifyApi);
  }

  @Bean
  public CachedTracksRepository createCachedTracksRepository(final JedisPool jedisPool, final ObjectMapper objectMapper) {
    return new CachedTracksRepositoryImpl(jedisPool, objectMapper);
  }
}

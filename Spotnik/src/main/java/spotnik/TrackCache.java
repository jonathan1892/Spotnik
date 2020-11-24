package spotnik;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackCache {

  private static final Logger LOG = LoggerFactory.getLogger(TrackCache.class);

  private final CachedTracksRepository cachedTracksRepository;
  private final SpotifyApi spotifyApi;

  public TrackCache(final CachedTracksRepository cachedTracksRepository, final SpotifyApi spotifyApi) {
    this.cachedTracksRepository = cachedTracksRepository;
    this.spotifyApi = spotifyApi;
  }

  Track getTrackByUri(final String uri) {
    LOG.info("Retrieving information from cache for uri={}", uri);
    final Optional<Track> cachedInfo = cachedTracksRepository.getTrackByUri(uri);

    if (cachedInfo.isPresent()) {
      LOG.info("Cache hit");
      return cachedInfo.get();
    } else {
      return fetchAndStoreTrackForUri(uri);
    }
  }

  private Track fetchAndStoreTrackForUri(final String uri) {
    LOG.info("Cache miss, fetching info from Spotify");
    final Track trackToCache = spotifyApi.findTrack(uri).block();

    if (trackToCache != null) {
      storeTrack(trackToCache);
      return trackToCache;
    } else {
      throw new IllegalArgumentException(uri);
    }
  }

  private void storeTrack(final Track track) {
    LOG.info("Caching information for track={}", track);
    cachedTracksRepository
        .storeTrack(track); // TODO: 2019-07-14 This will make the cache grow infinitely, decide on a strategy to fix this. Setting an expiration strategy in redis is probably good enough for now.
  }
}
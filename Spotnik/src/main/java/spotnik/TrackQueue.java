package spotnik;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackQueue {

  private static final Logger LOG = LoggerFactory.getLogger(TrackQueue.class);

  private final QueuedTracksRepository queuedTracksRepository;
  private final TrackCache trackCache;

  public TrackQueue(final QueuedTracksRepository queuedTracksRepository, final TrackCache trackCache) {
    this.queuedTracksRepository = queuedTracksRepository;
    this.trackCache = trackCache;
  }

  void queueTrack(final String uri) {
    final Track trackToQueue = trackCache.getTrackByUri(uri);

    if (!isSimilarTrackAlreadyQueued(trackToQueue)) {
      queuedTracksRepository.append(trackToQueue);
      LOG.info("Added to queue track={}", trackToQueue);
    } else {
      LOG.info("Similar track is already in queue, not adding track={}", trackToQueue);
    }
  }

  private boolean isSimilarTrackAlreadyQueued(final Track track) {
    return queuedTracksRepository.list().stream()
        .anyMatch(t -> t.isProbablyTheSameSong(track));
  }

  List<Track> list() {
    return queuedTracksRepository.list();
  }

  Optional<Track> popFirst() {
    if (!queuedTracksRepository.isEmpty()) {
      return Optional.of(queuedTracksRepository.popFirst());
    } else {
      return Optional.empty();
    }
  }
}

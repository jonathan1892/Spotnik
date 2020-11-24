package spotnik;

import java.util.Optional;

public interface CachedTracksRepository {

  void storeTrack(final Track track);

  Optional<Track> getTrackByUri(final String uri);

}

package spotnik;

import java.util.List;

public interface QueuedTracksRepository {

  void append(final Track track);

  Track popFirst();

  List<Track> list();

  boolean isEmpty();
}

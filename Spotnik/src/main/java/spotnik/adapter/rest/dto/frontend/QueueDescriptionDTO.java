package spotnik.adapter.rest.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import spotnik.QueueDescription;

public class QueueDescriptionDTO {

  @JsonProperty
  private QueueDTO queue;

  @JsonProperty
  private PlaylistDTO playlist;

  public QueueDescriptionDTO(final QueueDTO queue, final PlaylistDTO playlist) {
    this.queue = queue;
    this.playlist = playlist;
  }

  public static QueueDescriptionDTO from(final QueueDescription queueDescription) {
    return new QueueDescriptionDTO(
        QueueDTO.from(queueDescription.getQueuedTracks()),
        PlaylistDTO.from(queueDescription.getPlaylistTracks(), queueDescription.getPlaylistName())
    );
  }

}

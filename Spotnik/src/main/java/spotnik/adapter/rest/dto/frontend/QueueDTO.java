package spotnik.adapter.rest.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import spotnik.PendingTrack;

public class QueueDTO {

  @JsonProperty
  private List<PendingTrackDTO> items;

  public QueueDTO(final List<PendingTrackDTO> items) {
    this.items = items;
  }

  static QueueDTO from(final List<PendingTrack> pendingTracks) {
    return new QueueDTO(pendingTracks.stream().map(PendingTrackDTO::from).collect(Collectors.toList()));
  }
}

package spotnik.adapter.rest;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spotnik.ActiveTrack;
import spotnik.Conductor;
import spotnik.SpotifyApi;
import spotnik.SpotifyPlayer;
import spotnik.adapter.rest.dto.frontend.ActiveSpotifyTrackDTO;
import spotnik.adapter.rest.dto.frontend.QueueDescriptionDTO;
import spotnik.adapter.rest.dto.frontend.SpotifyTrackDTO;

@RestController
public class SpotnikController {

  private final SpotifyApi spotifyApi;
  private final Conductor conductor;
  private final SpotifyPlayer spotifyPlayer;
  private final SpotifyCredentials spotifyCredentials;

  public SpotnikController(final Conductor conductor, final SpotifyPlayer spotifyPlayer, final SpotifyApi spotifyApi,
      final SpotifyCredentials spotifyCredentials) {
    this.conductor = conductor;
    this.spotifyPlayer = spotifyPlayer;
    this.spotifyApi = spotifyApi;
    this.spotifyCredentials = spotifyCredentials;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public void healthCheck() {
  }

  @RequestMapping(value = "/api/health", method = RequestMethod.GET)
  public HealthReport getHealthReport() {
    return new HealthReport(spotifyCredentials.hasAccessToken());
  }

  @RequestMapping(value = "/api/playing", method = RequestMethod.GET)
  public ActiveSpotifyTrackDTO peekPlaying() {
    final Optional<ActiveTrack> activeSpotifyTrack = spotifyPlayer.getActiveTrack();
    return activeSpotifyTrack
        .map(ActiveSpotifyTrackDTO::fromActiveSpotifyTrack)
        .orElse(null);
  }

  @RequestMapping(value = "/api/search", method = RequestMethod.GET)
  public List<SpotifyTrackDTO> search(@RequestParam(value = "q") final String query) {
    return spotifyApi.findTracks(query)
        .map(SpotifyTrackDTO::fromSpotifyTrack)
        .collectList()
        .block();
  }

  @RequestMapping(value = "/api/queue", method = RequestMethod.PUT)
  public void queueTrack(@RequestParam(value = "uri") final String uri) {
    conductor.queueTrack(uri);
  }

  @RequestMapping(value = "/api/queue", method = RequestMethod.GET)
  public QueueDescriptionDTO peekQueue() {
    return QueueDescriptionDTO.from(conductor.describe());
  }

  @RequestMapping(value = "/api/admin/playlist", method = RequestMethod.PUT)
  public void queuePlaylist(@RequestParam(value = "uri") final String uri) {
    conductor.setFallbackPlaylist(uri);
  }

  @RequestMapping(value = "/api/admin/playlist", method = RequestMethod.DELETE)
  public void clearPlaylist() {
    conductor.clearFallbackPlaylist();
  }

  @RequestMapping(value = "/api/admin/queue/next")
  public void playNextTrack() {
    conductor.forcePlay();
  }
}

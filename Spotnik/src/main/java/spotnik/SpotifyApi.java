package spotnik;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpotifyApi {

  Mono<Void> playTrack(Track track);

  Mono<ActiveTrack> getActiveTrack();

  Flux<Track> findTracks(String query);

  Mono<Track> findTrack(String trackUri);

  Mono<Track> getRecommendedTrackBasedOn(Track track);

  Mono<String> getPlaylistName(String playlistUri);

  Flux<Track> getTracksInPlaylist(String playlistUri);
}

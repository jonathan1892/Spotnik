package spotnik.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spotnik.ActiveTrack;
import spotnik.SpotifyApi;
import spotnik.Track;
import spotnik.adapter.rest.dto.spotify.ActiveTrackDTO;
import spotnik.adapter.rest.dto.spotify.PlayTrackRequestDTO;
import spotnik.adapter.rest.dto.spotify.PlaylistDTO;
import spotnik.adapter.rest.dto.spotify.PlaylistTrackItemDTO;
import spotnik.adapter.rest.dto.spotify.PlaylistTracksDTO;
import spotnik.adapter.rest.dto.spotify.RecommendedTracksDTO;
import spotnik.adapter.rest.dto.spotify.SearchResultDTO;
import spotnik.adapter.rest.dto.spotify.TrackDTO;

public class SpotifyApiImpl implements SpotifyApi {

  private static final Logger LOG = LoggerFactory.getLogger(SpotifyApiImpl.class);

  private final SpotifyCredentials credentials;
  private final WebClient webClient;

  public SpotifyApiImpl(final SpotifyCredentials credentials, final WebClient.Builder webClientBuilder) {
    this.credentials = credentials;
    this.webClient = webClientBuilder
        .baseUrl("https://api.spotify.com/v1")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Override
  public Mono<Void> playTrack(final Track track) {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send PlayTrack request, no access token");
      return Mono.empty();
    }

    LOG.info("Sending PlayTrack request with track={}", track);
    return webClient.put()
        .uri("/me/player/play")
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .syncBody(new PlayTrackRequestDTO(track.getUri()))
        .exchange()
        .doOnNext(response -> {
          if (response.statusCode() == HttpStatus.NO_CONTENT) {
            LOG.info("PlayTrack httpStatus={}", response.statusCode());
          } else {
            LOG.warn("PlayTrack httpStatus={}", response.statusCode());
          }
        })
        .flatMap(response -> response.bodyToMono(Void.class))
        .then();
  }

  private String generateAuthenticationHeaderPayload() {
    return "Bearer " + credentials.getAccessToken();
  }

  @Override
  public Mono<ActiveTrack> getActiveTrack() {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send GetActiveTrack request, no access token");
      return Mono.empty();
    }

    LOG.info("Sending GetActiveTrack request");
    return webClient.get()
        .uri("/me/player/currently-playing")
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .exchange()
        .doOnNext(response -> LOG.info("GetActiveTrack httpStatus={}", response.statusCode()))
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(ActiveTrackDTO.class))
        .map(ActiveTrackDTO::toActiveSpotifyTrack);
  }

  @Override
  public Flux<Track> findTracks(final String query) {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send FindTracks request, no access token");
      return Flux.empty();
    }

    LOG.info("Sending FindTracks request with query={}", query);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/search")
            .queryParam("q", query)
            .queryParam("type", "track")
            .queryParam("market", "SE")
            .queryParam("limit", 20)
            .build())
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .exchange()
        .doOnNext(response -> LOG.info("FindTracks httpStatus={}", response.statusCode()))
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(SearchResultDTO.class))
        .flatMapIterable(searchResultDTO -> searchResultDTO.getTracks().getTrackItems())
        .map(TrackDTO::toSpotifyTrack);
  }

  @Override
  public Mono<Track> findTrack(final String trackUri) {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send FindTrack request, no access token");
      return Mono.empty();
    }

    final String trackId = trackUri.split(":")[2];
    LOG.info("Sending FindTrack request with id={}", trackId);
    return webClient.get()
        .uri("/tracks/" + trackId)
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .exchange()
        .doOnNext(response -> LOG.info("FindTrack httpStatus={}", response.statusCode()))
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(TrackDTO.class))
        .map(TrackDTO::toSpotifyTrack);
  }

  @Override
  public Mono<Track> getRecommendedTrackBasedOn(final Track seedTrack) {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send GetRecommendedTrack request, no access token");
      return Mono.empty();
    }

    LOG.info("Sending GetRecommendedTrack request with seedTrack={}", seedTrack);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/recommendations")
            .queryParam("limit", "1")
            .queryParam("market", "SE")
            .queryParam("seed_tracks", seedTrack.getId())
            .queryParam("min_popularity", "50")
            .build())
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .exchange()
        .doOnNext(response -> LOG.info("GetRecommendedTrack httpStatus={}", response.statusCode()))
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(RecommendedTracksDTO.class))
        .flatMapIterable(RecommendedTracksDTO::getTracks)
        .next()
        .map(TrackDTO::toSpotifyTrack);
  }

  @Override
  public Mono<String> getPlaylistName(final String playlistUri) {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send GetPlaylistName request, no access token");
      return Mono.empty();
    }

    LOG.info("Sending GetPlaylistName request for playlistUri={}", playlistUri);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/playlists/{id}")
            .build(playlistUri))
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .exchange()
        .doOnNext(response -> LOG.info("GetPlaylistName httpStatus={}", response.statusCode()))
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(PlaylistDTO.class))
        .map(PlaylistDTO::getName);
  }

  @Override
  public Flux<Track> getTracksInPlaylist(final String playlistUri) {
    if (!credentials.hasAccessToken()) {
      LOG.warn("Unable to send GetTracksInPlaylist request, no access token");
      return Flux.empty();
    }

    LOG.info("Sending GetTracksInPlaylist request for playlistUri={}", playlistUri);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/playlists/{id}/tracks")
            .build(playlistUri))
        .header(HttpHeaders.AUTHORIZATION, generateAuthenticationHeaderPayload())
        .exchange()
        .doOnNext(response -> LOG.info("GetTracksInPlaylist httpStatus={}", response.statusCode()))
        .filter(response -> response.statusCode() == HttpStatus.OK)
        .flatMap(response -> response.bodyToMono(PlaylistTracksDTO.class))
        .flatMapIterable(PlaylistTracksDTO::getPlaylistTrackItemDTO)
        .map(PlaylistTrackItemDTO::getTrack)
        .map(TrackDTO::toSpotifyTrack);
  }
}

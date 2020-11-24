package spotnik.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SpotifyCredentialsController {

  private final SpotifyCredentials spotifyCredentials;

  public SpotifyCredentialsController(final SpotifyCredentials spotifyCredentials) {
    this.spotifyCredentials = spotifyCredentials;
  }

  @RequestMapping(value = "/admin/login")
  public Object logIn(final RedirectAttributes attributes) {
    if (!spotifyCredentials.hasAccessToken()) {
      final String redirectUrl = spotifyCredentials.requestSpotifyAuthorization();
      attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
      attributes.addAttribute("attribute", "redirectWithRedirectView");
      return new RedirectView(redirectUrl);
    } else {
      return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
  }

  @RequestMapping(value = "/admin/logout")
  public ResponseEntity<Void> logOut() {
    spotifyCredentials.deleteTokens();
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/authorize")
  public String authorizationCallback(@RequestParam(value = "state") final String state,
      @RequestParam(value = "error", defaultValue = "") final String error,
      @RequestParam(value = "code", defaultValue = "") final String code) {

    if (error.equals("") && !code.equals("")) {
      spotifyCredentials.handleAuthorizationGranted(state, code);
      return "Spotnik has been successfully granted authorization to modify your Spotify playback state!";

    } else if (!error.equals("") && code.equals("")) {
      spotifyCredentials.handleAuthorizationDenied(state);
      return
          "Spotnik has not been granted authorization to modify your Spotify playback state! Please try again."
              + "Received error code from Spotify: " + error;

    } else {
      throw new RuntimeException("Received unexpected response parameters!");
    }
  }
}

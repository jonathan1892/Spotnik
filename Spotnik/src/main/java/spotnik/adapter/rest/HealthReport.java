package spotnik.adapter.rest;

public class HealthReport {

  private final boolean hasAccessToken;

  public HealthReport(final boolean hasAccessToken) {
    this.hasAccessToken = hasAccessToken;
  }

  public boolean getHasAccessToken() {
    return hasAccessToken;
  }
}
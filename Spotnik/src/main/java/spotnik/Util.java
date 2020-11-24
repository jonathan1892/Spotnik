package spotnik;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Util {

  private static final Logger LOG = LoggerFactory.getLogger(Util.class);

  private Util() {
  }

  static void sleep(final Duration delay) {
    try {
      TimeUnit.SECONDS.sleep(delay.toSeconds());
    } catch (final InterruptedException e) {
      LOG.warn("Sleep interrupted!");
      Thread.currentThread().interrupt();
    }
  }
}

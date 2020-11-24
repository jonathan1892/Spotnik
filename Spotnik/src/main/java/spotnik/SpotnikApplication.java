package spotnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import spotnik.adapter.rest.SpotifyCredentials;

@SpringBootApplication
public class SpotnikApplication {

  public static void main(final String[] args) {
    final ConfigurableApplicationContext context = SpringApplication.run(SpotnikApplication.class, args);
    context.getBean(SpotifyPlayer.class).startDaemonThreads();
    context.getBean(Conductor.class).startToConduct();
    context.getBean(SpotifyCredentials.class).setOnAuthenticatedCallback(context.getBean(SpotifyPlayer.class)::initPlayerWithExistingState);
  }
}

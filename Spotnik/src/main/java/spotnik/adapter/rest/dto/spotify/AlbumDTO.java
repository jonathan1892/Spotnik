package spotnik.adapter.rest.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumDTO {

  @JsonProperty
  private String uri;

  @JsonProperty
  private String name;

  @JsonProperty
  private List<ImageDTO> images;

  public String getUri() {
    return uri;
  }

  public String getName() {
    return name;
  }

  public List<ImageDTO> getImages() {
    return images;
  }
}

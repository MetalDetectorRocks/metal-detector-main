package rocks.metaldetector.discogs.client.transformer;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DiscogsArtistNameTransformer {

  private static final String DISCOGS_NAME_REGEX = "\\s*\\(\\d+\\)$";

  public String transformArtistName(String artistName) {
    Pattern pattern = Pattern.compile(DISCOGS_NAME_REGEX);
    Matcher matcher = pattern.matcher(artistName);
    return matcher.find() ? artistName.replace(matcher.group(0), "") : artistName;
  }
}

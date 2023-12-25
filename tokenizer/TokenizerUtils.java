import java.util.*;

public class TokenizerUtils
{
  protected String source;
  private final String[] KEYWORDS = {"fim", "se"};
  public int position = 0;

  public boolean is_keyword(String identifier)
  {
    return Arrays.binarySearch(KEYWORDS, identifier) >= 0;
  }

  public boolean can_consume()
  {
    System.out.println(source.length() + " " + position + " - " + (position == source.length() - 1));
    return !(position >= source.length() - 1);
  }

  public Character get_current_letter()
  {
    return source.charAt(position);
  }

  public Character consume_letter()
  {
    if(!can_consume())
      return get_letter();

    return source.charAt(++position);
  }

  public String get_string(int start)
  {
    return source.substring(start, position);
  }
}

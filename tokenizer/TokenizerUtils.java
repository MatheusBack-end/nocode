import java.util.*;

public class TokenizerUtils
{
  protected String source;
  private final String[] KEYWORDS = {"args", "criar", "diferente", "fim", "igual", "maior", "menor", "nulo", "pacote", "retornar", "se"};
  private final char[] OPERATORS = {'*', '+', '-', '/',  '='};
  private final char[] DELIMITERS = {'(', ')'};
  private final Token.Types[] DELIMITERS_TYPES = {Token.Types.OPARAM, Token.Types.CPARAM};
  private final char[] QUOTES = {'"'};
  public int position = 0;

  public boolean is_keyword(String identifier)
  {
    return Arrays.binarySearch(KEYWORDS, identifier) >= 0;
  }

  public boolean is_operator(Character letter)
  {
    //System.out.println(letter + " " + (Arrays.binarySearch(OPERATORS, (char) letter) >= 0));
    return Arrays.binarySearch(OPERATORS, (char) letter) >= 0;
  }

  public boolean is_quotes(Character letter)
  {
    return Arrays.binarySearch(QUOTES, (char) letter) >= 0;
  }

  public boolean is_delimiter(Character letter)
  {
    return Arrays.binarySearch(DELIMITERS, (char) letter) >= 0;
  }

  public boolean is_dot(Character letter)
  {
    return letter == '.';
  }
  
  public boolean is_block(Character letter)
  {
    return letter == ':';
  }

  public Token.Types get_delimiter_type(Character letter)
  {
    int index = Arrays.binarySearch(DELIMITERS, (char) letter);

    if(index >= 0)
      return DELIMITERS_TYPES[index];

    return null;
  }

  public boolean can_consume()
  {
    return !(position >= source.length() - 1);
  }

  public Character get_current_letter()
  {
    return source.charAt(position);
  }

  public Character consume_letter()
  {
    if(!can_consume())
      return null;

    return source.charAt(++position);
  }

  public Token get_token(String value, Token.Types type)
  {
    Token token = new Token(value, type);
    position++;

    return token;
  }

  public String get_string(int start)
  {
    if(!can_consume())
      return source.substring(start, ++position);

    return source.substring(start, position);
  }
}

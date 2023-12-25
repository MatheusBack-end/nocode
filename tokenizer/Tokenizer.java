import java.util.*;

public class Tokenizer extends TokenizerUtils
{
  public Tokenizer(String source)
  {
    super.source = source;
  }

  public Token[] get_tokens()
  {
    List<Token> tokens = new ArrayList<Token>();
    Token token = get_next_token();

    while(!token.type.equals("eof"))
    {
      tokens.add(token);
      token = get_next_token();
    }

    tokens.add(token);

    for(Token current_token: tokens)
    {
      System.out.println(current_token.type + " [" + current_token.value + "]");
    }

    return tokens.toArray(new Token[0]);
  }

  public Token get_next_token()
  {
    if(!can_consume())
      return new Token(null, "eof");

    Character letter = get_current_letter();

    while(Character.isWhitespace(letter) || letter.equals('\n'))
    {
      if(!can_consume())
        return new Token(null, "eof");

      letter = consume_letter();
    }

    if(Character.isLetter(letter))
    {
      int start = position;
      
      while(Character.isLetterOrDigit(letter) || letter.equals('_'))
      {
        letter = consume_letter();

        if(letter == null)
          break;
      }

      String identifier = get_string(start);

      if(is_keyword(identifier))
        return new Token(identifier, "keyword");

      return new Token(identifier, "identifier");
    }

    Token unknow_token = new Token(letter.toString(), "unknow token");
    consume_letter();

    return unknow_token;
  }
}

import java.util.*;

public class Tokenizer extends TokenizerUtils
{
  public Tokenizer(String source)
  {
    super.source = source;
  }

  public Token[] get_tokens()
  {
    long start_time = System.currentTimeMillis();

    List<Token> tokens = new ArrayList<Token>();
    Token token = get_next_token();

    while(!(token.type == Token.Types.EOF))
    {
      tokens.add(token);
      token = get_next_token();
    }

    tokens.add(token);

    for(Token current_token: tokens)
    {
      System.out.println(current_token.type + " [" + current_token.value + "]");
    }

    System.out.println(System.currentTimeMillis() - start_time);
    return tokens.toArray(new Token[0]);
  }

  public Token get_next_token()
  {
    //System.out.println(get_current_letter());
    if(position >= source.length())
      return new Token(null, Token.Types.EOF);

    Character letter = get_current_letter();

    while(Character.isWhitespace(letter) || letter.equals('\n'))
    {
      if(!can_consume())
        return new Token(null, Token.Types.EOF);

      letter = consume_letter();
    }

    if(Character.isLetter(letter))
    {
      int start = position;
      
      while(Character.isLetterOrDigit(letter) || letter.equals('_'))
      {
        letter = consume_letter();

        if(letter == null)
        {
          break;
        }
      }

      String identifier = get_string(start);
      
      //if(!identifier.equals("m"))
        //System.out.println("debug: " + identifier + ";");

      if(is_keyword(identifier))
        return new Token(identifier, Token.Types.KEYWORD);

      return new Token(identifier, Token.Types.IDENTIFIER);
    }

    if(Character.isDigit(letter))
    {
      int start = position;

      while(Character.isDigit(letter))
      {
        letter = consume_letter();

        if(letter == null)
          break;
      }

      return new Token(get_string(start), Token.Types.NUMBER);
    }

    if(is_dot(letter))
      return get_token(".", Token.Types.DOT);

    if(is_block(letter))
      return get_token(":", Token.Types.BLOCK);

    if(is_operator(letter))
    {
      Token operator = new Token(letter.toString(), Token.Types.OPERATOR);
      
      if(can_consume())
        consume_letter();

      return operator;
    }

    if(is_delimiter(letter))
    {
      Token.Types type = get_delimiter_type(letter);

      if(type == Token.Types.OPARAM)
        return get_token(letter.toString(), Token.Types.OPARAM);
      
      return get_token(letter.toString(), Token.Types.CPARAM);
    }

    if(is_quotes(letter))
    {
      if(!can_consume())
        return new Token(null, Token.Types.EOF);

      letter = consume_letter();

      int start = position;

      while(!is_quotes(letter))
      {
        letter = consume_letter();

        if(letter == null)
          break;
      }

      String string = get_string(start);
      position++;

      return new Token(string, Token.Types.STRING);
    }

    position++;
    return get_next_token();
  }
}

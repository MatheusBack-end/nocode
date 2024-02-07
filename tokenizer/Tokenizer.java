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
    if(position >= source.length())
      return new Token(null, Token.Types.EOF, new Loc(source.length(), source.length(), current_line));

    Character letter = get_current_letter();

    while(Character.isWhitespace(letter) || letter.equals('\n'))
    {
      if(!can_consume())
        return new Token(null, Token.Types.EOF, new Loc(source.length(), source.length(), current_line));

      letter = consume_letter();
      if(letter.equals('\n')) current_line++;
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

      if(is_keyword(identifier))
        return new Token(identifier, Token.Types.KEYWORD, new Loc(start, position, current_line));


      return new Token(identifier, Token.Types.IDENTIFIER, new Loc(start, position, current_line));
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

      return new Token(get_string(start), Token.Types.NUMBER, new Loc(start, position, current_line));
    }

    if(is_dot(letter))
      return get_token(".", Token.Types.DOT, new Loc(position, position, current_line));

    if(is_block(letter))
      return get_token(":", Token.Types.BLOCK, new Loc(position, position, current_line));

    if(is_operator(letter))
    {
      Token operator = new Token(letter.toString(), Token.Types.OPERATOR, new Loc(position, position, current_line));
      
      if(can_consume())
        consume_letter();

      return operator;
    }

    if(is_delimiter(letter))
    {
      Token.Types type = get_delimiter_type(letter);

      if(type == Token.Types.OPARAM)
        return get_token(letter.toString(), Token.Types.OPARAM, new Loc(position, position, current_line));
      
      return get_token(letter.toString(), Token.Types.CPARAM, new Loc(position, position, current_line));
    }

    if(is_quotes(letter))
    {
      if(!can_consume())
        return new Token(null, Token.Types.EOF, new Loc(position, position, current_line));

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

      return new Token(string, Token.Types.STRING, new Loc(start, position, current_line));
    }

    position++;
    return get_next_token();
  }
}

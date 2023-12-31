import java.util.*;

public class Parser
{
 /* public String text;
  private int pos = 0;

  public Parser(String text)
  {
    this.text = text;
  }

  public void get_tokens()
  {
    long start_time = System.currentTimeMillis();

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
      //System.out.println(current_token.type + " [" + current_token.value + "]");
    }

    System.out.println(System.currentTimeMillis() - start_time);
  }

  public Token get_next_token()
  {
    char letter = remove_whitespaces();

    if(pos == text.length())
    {
      return new Token("", "eof");
    }

    if(isAlphanumeric(letter))
    {
      Token identifier = consume_identifier(letter);

      if(identifier.value.equals("se"))
      {
        return new Token(identifier.value, "keyword");
      }

      if(identifier.value.equals("pacote"))
      {
        return new Token(identifier.value, "keyword");
      }

      if(identifier.value.equals("igual"))
      {
        return new Token(identifier.value, "operator");
      }

      if(identifier.value.equals("diferente"))
      {
        return new Token(identifier.value, "operator");
      }

      if(identifier.value.equals("nulo"))
      {
        return new Token(identifier.value, "nulo");
      }

      if(identifier.value.equals("criar"))
      {
        return new Token(identifier.value, "new");
      }

      if(identifier.value.equals("verdade"))
      {
        return new Token("true", "literal_boolean");
      }

      if(identifier.value.equals("falso"))
      {
        return new Token("false", "literal_boolean");
      }

      if(identifier.value.equals("args"))
      {
        return new Token(identifier.value, "keyword");
      }

      if(identifier.value.equals("retornar"))
      {
        return new Token(identifier.value, "keyword");
      }

      if(identifier.value.equals("menor"))
      {
        return new Token(identifier.value, "operator");
      }

      if(identifier.value.equals("maior"))
      {
        return new Token(identifier.value, "operator");
      }

      if(identifier.value.equals("fim"))
      {
        pos++;
        return new Token(identifier.value, "close_block");
      }

      // ignore this context token!
      if(identifier.value.equals("que"))
      {
        return get_next_token();
      }
      
      if(identifier.value.equals("de"))
      {
        return get_next_token();
      }

      return identifier;
    }

    if(is_number(letter))
    {
      return consume_number(letter);
    }

    if(letter == "\"".charAt(0))
    {
      return consume_string(letter);
    }

    if(letter == ',')
    {
      pos++;
      return new Token(",", "operator");
    }

    if(letter == "(".charAt(0))
    {
      pos++;
      return new Token("(", "oparam");
    }

    if(letter == ")".charAt(0))
    {
      ++pos;
      return new Token(")", "cparam");
    }

    if(letter == ":".charAt(0))
    {
      pos++;
      return new Token(":", "block");
    }
    
    if(letter == "=".charAt(0))
    {
      pos++;
      return new Token("=", "equals");
    }

    if(letter == "+".charAt(0))
    {
      if(consume_letter() == '+')
      {
        pos++;
        return new Token("++", "pp");
      }
      
      return new Token("+", "sum");
    }

    if(letter == "-".charAt(0))
    {
      pos++;
      return new Token("-", "sub");
    }

    if(letter == "/".charAt(0))
    {
      if(consume_letter() == "/".charAt(0));
      {  
        consume_comment(letter);
        return get_next_token();
      }
    }

    if(letter == '.')
    {
      pos++;
      return new Token(".", "dot");
    }

    if(pos < text.length() -2)
      consume_letter();

    return get_next_token();
  }

  public char consume_letter()
  {
    return text.charAt(++pos);
  }

  public char remove_whitespaces()
  {
    if(pos == text.length())
      return ' ';

    char letter = text.charAt(pos);

    while((letter == " ".charAt(0)) || (letter == "\n".charAt(0)))
    {
      if(pos == text.length() -1)
      {
        pos++;
        break;
      }

      letter = consume_letter();
    }

    return letter;
  }

  public Token consume_string(char letter)
  {
    String string = "";

    letter = consume_letter();

    while(letter != "\"".charAt(0))
    {
      string += Character.toString(letter);

      if(pos > text.length() -2)
        break;

      letter = consume_letter();
    }

    pos++;
    return new Token(string, "string");
  }

  public void consume_comment(char letter)
  {
    letter = consume_letter();

    while((letter != "\n".charAt(0)) && (pos < text.length() -2))
    {
      letter = consume_letter();
    }

    consume_letter();
  }

  public Token consume_identifier(char letter)
  {
    String identifier = "";

    while(isAlphanumeric(letter) || letter == '_')
    {
      identifier += Character.toString(letter);
        
      if(pos > text.length() -2)
        break;

      letter = consume_letter();
    }

    return new Token(identifier, "identifier");
  }

  public Token consume_number(char letter)
  {
    String number = "";

    while(is_number(letter))
    {
      number += Character.toString(letter);
        
      if(pos > text.length() -2)
        break;

      letter = consume_letter();
    }

    return new Token(number, "number");
  }


  public boolean isAlphanumeric(char letter)
  {
    return Character.isLetter(letter);
  }

  public boolean is_number(char letter)
  {
    return Character.isDigit(letter);
  }*/
}

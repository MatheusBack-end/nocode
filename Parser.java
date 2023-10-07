public class Parser
{
  public String text;
  private int pos = 0;

  public Parser(String text)
  {
    this.text = text;

    Token current_token = get_next_token();

    while(!current_token.type.equals("eof"))
    {
      System.out.println(current_token.type + " " + current_token.value);
      current_token = get_next_token();
    }
  }

  public Token get_next_token()
  {
    if(pos > text.length() -1)
    {
      return new Token("", "eof");
    }

    char letter = remove_whitespaces(text.charAt(pos));

    if(isAlphanumeric(letter))
    {
      return consume_identifier(letter);
    }

    if(letter == "\"".charAt(0))
    {
      return consume_string(letter);
    }

    if(letter == "(".charAt(0))
    {
      pos++;
      return new Token("(", "oparam");
    }

    if(letter == ")".charAt(0))
    {
      pos++;
      return new Token(")", "cparam");
    }

    Token token = new Token(Character.toString(letter), "letter");

    if(pos < text.length() -2)
      consume_letter();

    return token;
  }

  public char consume_letter()
  {
    return text.charAt(++pos);
  }

  public char remove_whitespaces(char letter)
  {
    while(letter == " ".charAt(0))
    {
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

  public Token consume_identifier(char letter)
  {
    String identifier = "";

    while(isAlphanumeric(letter))
    {
      identifier += Character.toString(letter);
        
      if(pos > text.length() -2)
        break;

      letter = consume_letter();
    }

    return new Token(identifier, "identifier");
  }

  public boolean isAlphanumeric(char letter)
  { 
    if(!Character.isLetterOrDigit(letter))
      return false;

    return true;
  }
}

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
      System.out.println(current_token);
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
      String identifier = "";

      while(isAlphanumeric(letter))
      {
        identifier += Character.toString(letter);
        
        if(pos > text.length() -1)
          break;

        letter = consume_letter();
      }

      return new Token(identifier, "identifier");
    }

    consume_letter();
    return new Token(Character.toString(letter), "letter");
  }

  public char consume_letter()
  {
    return text.charAt(pos++);
  }

  public char remove_whitespaces(char letter)
  {
    while(letter == " ".charAt(0))
    {
      letter = consume_letter();
    }

    return letter;
  }

  public boolean isAlphanumeric(char letter)
  { 
    if(!Character.isLetterOrDigit(letter))
      return false;

    return true;
  }
}

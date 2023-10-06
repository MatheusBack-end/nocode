public class Parser
{
  public String text;
  private int pos = 0;

  public Parser(String text)
  {
    this.text = text;
    get_next_token();
  }

  public Token get_next_token()
  {
    char letter = remove_whitespaces(text.charAt(pos));

    if(pos > text.length() -1)
    {
      return new Token();
    }

    System.out.println(letter);

    return null;
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
}

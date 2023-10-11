import java.util.*;

public class Consumer
{
  public Parser parser;
  public List<Token> tokens;
  public Token current_token;
  public boolean is_list = false;
  public int pos = 0;

  public Consumer(Parser parser)
  {
    this.parser = parser;
  }

  public Consumer(List<Token> tokens)
  {
    this.tokens = tokens;
    this.is_list = true;
  }

  public void consume_token(String... token_type)
  {
    consume_token();
  }

  public void consume_token()
  { 
    if(!is_list)
    {
      current_token = parser.get_next_token();
      return;
    }
    
    if(pos < tokens.size() -1)
      current_token = tokens.get(++pos);
  }
}

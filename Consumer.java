import java.util.*;

public class Consumer
{
  public Tokenizer parser;
  public List<Token> tokens;
  public Token current_token;
  public boolean is_list = false;
  public int pos = 0;

  public Consumer(Tokenizer parser)
  {
    this.parser = parser;
  }

  public Consumer(List<Token> tokens)
  {
    this.tokens = tokens;
    this.is_list = true;
  }

  public Token consume_token(Token.Types... token_types)
  {
    Token previous_token = current_token;

    for(Token.Types type: token_types)
    {
      if(current_token.type.equals(type))
      {
        consume_token();
        return previous_token;
      }
    }
    
    System.out.println("\u001B[31merro:\u001B[0m esperado " + token_types[0] + " recebido " + current_token.type);
    System.exit(1);
    return null;
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

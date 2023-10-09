import java.util.*;
import java.lang.reflect.*;

public class Functions
{
  public List<Token> tokens;
  public int pos;
  public Token current_token;
  public Interpreter interpreter;

  public Functions(List<Token> tokens, Interpreter interpreter)
  {
    this.tokens = tokens;
    this.interpreter = interpreter;
    call();
  }

  public void consume_token()
  {
    if(pos < tokens.size() -1)
      current_token = tokens.get(++pos);
  }

  public void call()
  {
    current_token = tokens.get(pos);

    while(pos < tokens.size() -1)
    {
      invoke();
    }
  }

  public void invoke()
  {
    if(current_token.type == "identifier")
    {
      Token identifier = current_token;
      consume_token();

      if(current_token.type == "oparam")
      {
        consume_token();

        List<String> args = new ArrayList<String>();

        while(current_token.type != "cparam")
        {
          args.add(current_token.value);
          consume_token();
        }

        consume_token();


        if(interpreter.functions.containsKey(identifier.value))
        {
          interpreter.invoke_function(identifier.value);
          return;
        }

        try
        {
          Method method = Interpreter.class.getMethod(identifier.value, String.class);
          method.invoke(interpreter, args.get(0));

          return;
        }

        catch(Exception e)
        {
          System.out.println(e);
        }
      }
      System.exit(0);
    }
  }
}

import java.util.*;
import java.lang.reflect.*;

public class Interpreter
{
  Parser parser;
  Token current_token;


  public void emita(String value)
  {
    System.out.println(value);
  }

  public Interpreter(Parser parser)
  {
    this.parser = parser;
    interpreter();
  }

  public void interpreter()
  {
    current_token = parser.get_next_token();
    while(eat()){}
  }

  public void consume_token(String... token_type)
  {
    for(String type: token_type)
    {
      if(current_token.type == type)
      {
        current_token = parser.get_next_token();
        return;
      }
    }

    System.out.println("token inesperado");
  }

  public boolean eat()
  {
    if(current_token.type == "identifier")
    {
      Token identifier = current_token;
      consume_token("identifier");

      if(current_token.type == "oparam")
      {
        consume_token("oparam");

        List<String> args = new ArrayList<String>();

        while(current_token.type != "cparam")
        {
          args.add((String) current_token.value);
          consume_token("identifier", "string");
        }

        try
        {
          Method method = Interpreter.class.getMethod(identifier.value, String.class);
          method.invoke(this, args.get(0));
        } 

        catch(Exception e)
        {
          System.out.println(e);
        }
      }
    }

    return false;
  }
}

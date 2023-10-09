import java.util.*;
import java.lang.reflect.*;

public class Functions
{
  public List<Token> tokens;
  public int pos;
  public Token current_token;
  public Interpreter interpreter;
  public List<String> args;
  public Map<String, String> local_variables = new HashMap<String, String>();
  public String name;

  public Functions(List<Token> tokens, List<String> args, Interpreter interpreter, String name)
  {
    this.tokens = tokens;
    this.args = args;
    this.interpreter = interpreter;
    this.name = name;
    link_local_variables();
    call();
  }

  public void link_local_variables()
  {
    int iota = 0;
    for(String arg: args)
    {
      local_variables.put(interpreter.function_parameters.get(name).get(iota), arg);
      iota++;
    }
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
          if(current_token.type.equals("identifier"))
          {
            if(!local_variables.containsKey(current_token.value))
            {
              System.out.println("variavel: \"" + current_token.value + "\" não foi definida >:[");
              System.exit(1);
            }

            args.add((String) local_variables.get(current_token.value));
          }

          else
          {
            args.add(current_token.value);
          }
          consume_token();
        }

        consume_token();


        if(interpreter.functions.containsKey(identifier.value))
        {
          interpreter.invoke_function(identifier.value, args);
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

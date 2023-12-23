import java.util.*;
import java.lang.reflect.*;

public class Functions extends InterpreterUtils
{
  public Interpreter interpreter;
  public List<Object> args;
  public Map<String, Object> local_variables = new HashMap<String, Object>();
  public String name;
  public Object return_value;

  public Functions(List<Token> tokens, List<Object> args, Interpreter interpreter, String name)
  {
    super(tokens);
    this.args = args;
    super.interpreter = interpreter;
    this.interpreter = interpreter;
    this.name = name;
    link_local_variables();
  }

  public void link_local_variables()
  {
    int iota = 0;
    for(Object arg: args)
    {
      super.variables.put(interpreter.function_parameters.get(name).get(iota), arg);
      iota++;
    }
  }


  public Object call()
  {
    current_token = tokens.get(pos);

    while(invoke())
    {

    }

    return return_value;
  }

  public boolean invoke()
  {
    if(!(pos < tokens.size() - 1))
    {
      return false;
    }

    if(current_token.type.equals("close_block"))
    {
      return true;
    }

    if((current_token.type.equals("keyword")) && (current_token.value.equals("retornar")))
    {
      consume_token();
      
      return_value = expression();
      return false;
    }

    if((current_token.type.equals("keyword")) && (current_token.value.equals("se")))
    {
      consume_token();

      boolean codition = Boolean.parseBoolean((String) expression());

      consume_token();

      if(!codition)
      {
        List<Token> if_block = new ArrayList<Token>();

        while(current_token.type != "close_block")
        {
          if_block.add(current_token);
          consume_token();
        }

        consume_token();

        return true;
      }

      return true;
    }

    if(current_token.type == "identifier")
    {
      Token identifier = current_token;
      consume_token();

      if(current_token.type == "oparam")
      {
        consume_token();

        List<Object> args = new ArrayList<Object>();

        while(current_token.type != "cparam")
        {
          args.add(expression());
        }

        consume_token();


        if(interpreter.functions.containsKey(identifier.value))
        {
          interpreter.invoke_function(identifier.value, args);
          return true;
        }

        try
        {
          Method method = Interpreter.class.getMethod(identifier.value, String.class);
          method.invoke(interpreter, args.get(0));

          return true;
        }

        catch(Exception e)
        {
          System.out.println(e);
        }
      }

      if(current_token.type == "equals")
      {
        consume_token();
        
        String value = (String) expression();

        local_variables.put(identifier.value, value);
        return true;
      }

      System.exit(0);
    }

    return false;
  }
}

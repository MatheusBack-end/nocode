import java.util.*;
import java.lang.reflect.*;
import java.lang.Class;

public class Interpreter extends InterpreterUtils
{
  Map<String, List<Token>> functions = new HashMap<String, List<Token>>();
  Map<String, List<String>> function_parameters = new HashMap<String, List<String>>();
  
  public void emita(String... values)
  {
    String full_value = "";

    for(String value: values)
    {
      full_value += value;
    }

    System.out.println(full_value);
  }

  public void emita(String value)
  {
    System.out.println(value);
  }

  public Interpreter(Parser parser)
  {
    super(parser);
    super.interpreter = this;

    interpreter();
  }

  public void interpreter()
  {
    current_token = parser.get_next_token();

    while(current_token.type != "eof")
    {
      eat();
    }
  }

  public boolean codition(List<Token> expression)
  {
    if(Boolean.parseBoolean(expression.get(0).value))
    {
      return true;
    }

    return false;
  }

  public boolean eat()
  {
    if(current_token.type == "close_block")
    {
      consume_token();
      return true;
    }

    if(current_token.type == "keyword")
    {
      consume_token("keyword");

      boolean codition = Boolean.parseBoolean(expression());

      consume_token("block");

      if(!codition)
      {
        List<Token> if_block = new ArrayList<Token>();

        while(current_token.type != "close_block")
        {
          if_block.add(current_token);
          consume_token();
        }

        consume_token("close_block");

        return true;
      }

      return true;
    }

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
          args.add(expression());
        }

        consume_token("cparam");

        if(functions.containsKey(identifier.value))
        {
          invoke_function(identifier.value, args);
          return true;
        }

        try
        {
          String arg_array[] = new String[args.size()];

          for(int i = 0; i < args.size(); i++)
          {
            arg_array[i] = args.get(i);
          }

          Method method = Interpreter.class.getMethod(identifier.value, String[].class);
          method.invoke(this, (Object) arg_array);

          return true;
        }

        catch(Exception e)
        {
          System.out.println(e);
        }
      }

      if(current_token.type == "equals")
      {
        consume_token("equals");
        
        String value = expression();

        variables.put(identifier.value, value);
        return true;
      }

      if(current_token.value.equals("args"))
      {
        consume_token("keyword");

        List<String> parameters = new ArrayList<String>();

        while(!current_token.type.equals("block"))
        {
          parameters.add(current_token.value);
          consume_token("identifier");
        }

        function_parameters.put(identifier.value, parameters);
      }

      if(current_token.type == "block")
      {
        consume_token("block");
        List<Token> function_block = new ArrayList<Token>();
        int scopes = 1;

        while(scopes != 0)
        {
          if(current_token.type.equals("block"))
          {
            scopes++;
          }

          if(current_token.type.equals("close_block"))
          {
            scopes--;
          }

          function_block.add(current_token);
          consume_token();
        }

        functions.put(identifier.value, function_block);
      }
    }

    return false;
  }

  public String invoke_function(String name, List<String> args)
  {
    Functions f = new Functions(functions.get(name), args, this, name);
    return f.call();
  }
}

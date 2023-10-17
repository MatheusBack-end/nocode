import java.util.*;
import java.lang.reflect.*;
import java.lang.Class;

@SuppressWarnings("deprecation")
public class Interpreter extends InterpreterUtils
{
  Map<String, List<Token>> functions = new HashMap<String, List<Token>>();
  Map<String, List<String>> function_parameters = new HashMap<String, List<String>>();
  
  public void emita(String... values)
  {
    String full_value = new java.lang.String("");

    for(String value: values)
    {
      full_value += value;
    }

    System.out.println(full_value);
  }

  public void print(String... value)
  {
    System.out.println(value[0]);
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

  @SuppressWarnings("deprecation")
  public boolean eat()
  {
    if(expected("new"))
    {
      create_instance();
      return true;
    }

    if(current_token.type == "close_block")
    {
      consume_token();
      return true;
    }

    if(current_token.type == "keyword")
    {
      consume_token("keyword");

      boolean codition = Boolean.parseBoolean((String) expression());

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

      List<String> method_loc = new ArrayList<String>();
/**
 *
 * Interpreter.get_stdout().emita()
 *
 */
      if(current_token.type == "dot")
      {
        consume_token("dot");

        Class source_class = null;

        try
        {
          source_class = Class.forName(identifier.value);
        }

        catch(Exception e)
        {
          System.out.println(e);
        }

        while(current_token.type.equals("identifier"))
        {
          Token name = current_token;
          consume_token("identifier");

          if(current_token.type.equals("dot"))
          {
            consume_token("dot");
            continue;
          }

          if(current_token.type.equals("oparam"))
          {
            consume_token("oparam");
            List<String> args = new ArrayList<String>();

            while(current_token.type != "cparam")
            {
              args.add((String) expression());
            }

            consume_token("cparam");

            try
            {
              @SuppressWarnings("deprecation")
              Method  method = source_class.getMethod(name.value, String.class);
              method.invoke(this, args.get(0));
            }

            catch(Exception e)
            {
              System.out.println(e);
            }

            if(!current_token.type.equals("dot"))
            {
              break;
            }
          }
        }

        for(String loc: method_loc)
        {
          System.out.println(loc);
        }
      }

      if(current_token.type == "oparam")
      {
        consume_token("oparam");

        List<String> args = new ArrayList<String>();

        while(current_token.type != "cparam")
        {
          args.add((String) expression());
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
        
        String value = (String) expression();

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

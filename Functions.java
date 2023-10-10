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
  String return_value;

  public Functions(List<Token> tokens, List<String> args, Interpreter interpreter, String name)
  {
    this.tokens = tokens;
    this.args = args;
    this.interpreter = interpreter;
    this.name = name;
    link_local_variables();
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

  public String call()
  {
    current_token = tokens.get(pos);

    while(invoke())
    {

    }

    return return_value;
  }

  public String expression()
  {
    if(current_token.type.equals("identifier"))
    {
      Token identifier = current_token;
      consume_token();

      if(current_token.type.equals("oparam"))
      {
        consume_token();

        List<String> args = new ArrayList<String>();
        while(!current_token.type.equals("cparam"))
        {
          args.add((String) expression());
          /*if(current_token.type.equals("identifier"))
          {
            System.out.println(current_token.type);
            if(!local_variables.containsKey(current_token.value))
            {
              System.out.println("variavel: \"" + current_token.value + "\" não foi definida >:(");
              System.exit(1);
            }
          }
          
          else
          {
            args.add((String) current_token.value);
          }

          consume_token();*/
        }

        consume_token();

        if(current_token.type.equals("sum"))
        {
          consume_token();
          String value_1 = interpreter.invoke_function(identifier.value, args);
          String value_2 = expression();
          
          return String.valueOf(Integer.valueOf(value_1) + Integer.valueOf(value_2));
        }

        if(interpreter.functions.containsKey(identifier.value))
        {
          return interpreter.invoke_function(identifier.value, args);
        }

        try
        {
          Method method = Interpreter.class.getMethod(identifier.value, String.class);
          method.invoke(interpreter, args.get(0));

          return "";
        } 

        catch(Exception e)
        {
          System.out.println(e);
        }

      }

      if(current_token.type.equals("sub"))
      {
        consume_token();

        if(current_token.type.equals("string"))
        {
          int result = Integer.valueOf(local_variables.get(identifier.value)) - Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(result);
        }
      }

      if(current_token.type.equals("operator"))
      {
        if(current_token.value.equals("menor"))
        {
          consume_token();
          boolean operation = Integer.valueOf(local_variables.get(identifier.value)) < Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(operation);
        }
      }

      if(!local_variables.containsKey(identifier.value))
      {
        System.out.println("variavel: \"" + identifier.value + "\" não foi definida >:/");
        System.exit(1);
      }

      else
      {
        return local_variables.get(identifier.value);
      }
    }

    if(current_token.type.equals("string"))
    {
      Token value = current_token;
      consume_token();

      if(current_token.type.equals("operator"))
      {
        if(current_token.value.equals("menor"))
        {
          consume_token();

          boolean operation = Integer.valueOf(value.value) < Integer.valueOf(current_token.value);

          consume_token();

          return String.valueOf(operation);
        }

        if(current_token.value.equals("maior"))
        {
          consume_token();

          boolean operation = Integer.valueOf(value.value) > Integer.valueOf(current_token.value);

          consume_token();

          return String.valueOf(operation);
        }
      }

      if((current_token.type.equals("identifier")) || (current_token.type.equals("cparam")))
      {
        return value.value;
      }
    }

    return "";
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
      String value = expression();

      return_value = value;
      return false;
    }

    if((current_token.type.equals("keyword")) && (current_token.value.equals("se")))
    {
      consume_token();

      boolean codition = Boolean.parseBoolean(expression());

      //System.out.println(codition);
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

        List<String> args = new ArrayList<String>();

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
      System.exit(0);
    }

    return false;
  }
}

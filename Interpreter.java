import java.util.*;
import java.lang.reflect.*;

public class Interpreter
{
  Parser parser;
  Token current_token;
  Map<String, String> variables = new HashMap<String, String>();
  Map<String, List<Token>> functions = new HashMap<String, List<Token>>();
  Map<String, List<String>> function_parameters = new HashMap<String, List<String>>();
  
  public void emita(String value)
  {
    System.out.println(value);
  }

  public Interpreter(Parser parser)
  {
    this.parser = parser;
    variables.clear();

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

  public void consume_token()
  {
    current_token = parser.get_next_token();
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

      List<Token> expression = new ArrayList<Token>();

      while(current_token.type != "block")
      {
        expression.add(current_token);
        consume_token();
      }

      consume_token("block");

      if(!codition(expression))
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
          if(current_token.type == "identifier")
          {
            if(!variables.containsKey(current_token.value))
            {
              System.out.println("variavel: \"" + current_token.value + "\" não foi definida >:[");
              System.exit(1);
            }
            args.add((String) variables.get(current_token.value));
          }

          else
          {
            args.add((String) current_token.value);
          }

          consume_token("identifier", "string");
        }

        consume_token("cparam");

        if(functions.containsKey(identifier.value))
        {
          invoke_function(identifier.value, args);
          return true;
        }

        try
        {
          Method method = Interpreter.class.getMethod(identifier.value, String.class);
          method.invoke(this, args.get(0));

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
        
        Token value = current_token;
        consume_token("string");

        variables.put(identifier.value, value.value);
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

        while(current_token.type != "close_block")
        {
          function_block.add(current_token);
          consume_token();
        }

        consume_token("close_block");
        functions.put(identifier.value, function_block);
      }
    }

    return false;
  }

  public void invoke_function(String name, List<String> args)
  {
    new Functions(functions.get(name), args, this, name);
  }
}

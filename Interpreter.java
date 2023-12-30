import java.util.*;
import java.lang.reflect.*;
import java.lang.Class;

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

  public void print(int value)
  {
    System.out.println(value);
  }
  
  public void print(Integer value)
  {
    System.out.println(value);
  }
  
  public void emita(String value)
  {
    System.out.println(value);
  }

  public Interpreter(Tokenizer parser)
  {
    super(parser);
    super.interpreter = this;

    interpreter();
  }

  public void interpreter()
  {
    current_token = parser.get_next_token();

    while(current_token.type != Token.Types.EOF)
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

  public void import_package()
  {
    consume_token(Token.Types.KEYWORD);
    
    String package_name = current_token.value;
    super.package_names.add(package_name);

    consume_token(Token.Types.STRING);
  }

  public boolean eat()
  {
    if(current_token.type == Token.Types.KEYWORD)
    {
      if(current_token.value.equals("new"))
      {
        create_instance();
        return true;
      }
    }

    if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("pacote"))
    {
      import_package();
      return true;
    }

    if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("fim"))
    {
      consume_token();
      return true;
    }

    if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("se"))
    {
      consume_token(Token.Types.KEYWORD);

      boolean codition = (boolean) expression();

      consume_token(Token.Types.BLOCK);

      if(!codition)
      {
        List<Token> if_block = new ArrayList<Token>();

        while(current_token.type != Token.Types.KEYWORD && !(current_token.value.equals("fim")))
        {
          if_block.add(current_token);
          consume_token();

          if(current_token.type == Token.Types.EOF)
            return true;
        }

        consume_token(Token.Types.KEYWORD);

        return true;
      }

      return true;
    }

    if(current_token.type == Token.Types.IDENTIFIER)
    {
      Token identifier = current_token;
      consume_token(Token.Types.IDENTIFIER);

      List<String> method_loc = new ArrayList<String>();

      if(current_token.type == Token.Types.DOT)
      {
        EvalDot dot_exp = new EvalDot(this, identifier);
        dot_exp.eval();
      }

      if(current_token.type == Token.Types.OPARAM)
      {
        List<Object> args = get_args();

        if(functions.containsKey(identifier.value))
        {
          invoke_function(identifier.value, args);
          return true;
        }

        Class[] arg_types = get_arg_types((Object[]) args.toArray(new Object[0]));

        try
        {
          Method method = Interpreter.class.getMethod(identifier.value, arg_types);
          method.invoke(this, (Object[]) args.toArray(new Object[0]));

          return true;
        }

        catch(Exception e)
        {
          System.out.println(e);
        }
      }

      if(current_token.type == Token.Types.OPERATOR && (current_token.value.equals("=")))
      {
        consume_token(Token.Types.OPERATOR);
        
        Object value = expression();

        variables.put(identifier.value, value);
        return true;
      }

      if(current_token.value.equals("args"))
      {
        consume_token(Token.Types.KEYWORD);

        List<String> parameters = new ArrayList<String>();

        while(!(current_token.type == Token.Types.BLOCK))
        {
          parameters.add(current_token.value);
          consume_token(Token.Types.IDENTIFIER);
        }

        function_parameters.put(identifier.value, parameters);
      }

      if(current_token.type == Token.Types.BLOCK)
      {
        consume_token(Token.Types.BLOCK);
        List<Token> function_block = new ArrayList<Token>();
        int scopes = 1;

        while(scopes != 0)
        {
          if(current_token.type == Token.Types.BLOCK)
          {
            scopes++;
          }

          if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("fim"))
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

  public Object invoke_function(String name, List<Object> args)
  {
    Functions f = new Functions(functions.get(name), args, this, name);
    return f.call();
  }
}

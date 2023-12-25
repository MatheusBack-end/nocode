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

  public void import_package()
  {
    consume_token("keyword");
    
    String package_name = current_token.value;
    super.package_names.add(package_name);

    consume_token("string");
  }

  public boolean eat()
  {
    if(expected("new"))
    {
      create_instance();
      return true;
    }

    if(current_token.type.equals("keyword") && current_token.value.equals("pacote"))
    {
      import_package();
      return true;
    }

    if(current_token.type == "close_block")
    {
      consume_token();
      return true;
    }

    if(current_token.type == "keyword" && current_token.value.equals("se"))
    {
      consume_token("keyword");

      boolean codition = (boolean) expression();


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

      if(current_token.type == "dot")
      {
        EvalDot dot_exp = new EvalDot(this, identifier);
        dot_exp.eval();
      }

      if(current_token.type == "oparam")
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

      if(current_token.type == "equals")
      {
        consume_token("equals");
        
        Object value = expression();

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

  public Object invoke_function(String name, List<Object> args)
  {
    Functions f = new Functions(functions.get(name), args, this, name);
    return f.call();
  }
}

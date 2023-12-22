import java.util.*;
import java.lang.reflect.*;

@SuppressWarnings("deprecation")
public class InterpreterUtils extends Consumer
{

  public Map<String, Object> variables = new HashMap<String, Object>();
  public Interpreter interpreter;

  public InterpreterUtils(Parser parser)
  {
    super(parser);
  }

  public InterpreterUtils(List<Token> tokens)
  {
    super(tokens);
  }

  public boolean expected(String token_type)
  {
    if(!current_token.type.equals(token_type))
      return false;

    consume_token(token_type);
    return true;
  }

  public Object create_instance()
  {
    Token java_class_name = consume_token("identifier");
    String result = "";
    result += java_class_name.value;

    if(current_token.type.equals("dot"))
    {
      List<Token> trace = new ArrayList<Token>();

      while(!current_token.type.equals("oparam"))
      {
        trace.add(current_token);
        consume_token();
      }
  
      for(Token t: trace)
      {
        result += t.value;
      }
    }

    consume_token("oparam");

    List<String> args = new ArrayList<String>();

    while(!current_token.type.equals("cparam"))
    {
      args.add((String) expression());
    }

    consume_token("cparam");

    Object class_instance = null;

    try
    {
      Class[] types = new Class[args.size()];
      String[] args_array = new String[args.size()];

      for(int i = 0; i < types.length; i++)
      {
        types[i] = args.get(i).getClass();
        args_array[i] = args.get(i);
      }

      class_instance = Class.forName(result).getConstructor(types).newInstance((Object[]) args_array);      
    }

    catch(Exception e)
    {
      System.out.println(e);
    }

    return class_instance;
  }

  public Object expression()
  {
    if(expected("new"))
    {
      return create_instance();
    }

    if(current_token.type.equals("number"))
    {
      int number = Integer.parseInt(current_token.value);
      consume_token("number");

      return (int) number;
    }

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
        }

        consume_token();

        if(current_token.type.equals("sum"))
        {
          consume_token();
          String value_1 = interpreter.invoke_function(identifier.value, args);
          String value_2 = (String) expression();
          
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
          int result = Integer.valueOf((String) variables.get(identifier.value)) - Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(result);
        }
      }

      if(current_token.type.equals("sum"))
      {
        consume_token();

        if(current_token.type.equals("string"))
        {
          int result = Integer.valueOf((String) variables.get(identifier.value)) + Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(result);
        }
      } 

      if(current_token.type.equals("operator"))
      {
        if(current_token.value.equals("menor"))
        {
          consume_token();
          boolean operation = Integer.valueOf((String) variables.get(identifier.value)) < Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(operation);
        }
      }

      if(!variables.containsKey(identifier.value))
      {
        System.out.println("variavel: \"" + identifier.value + "\" não foi definida >:/");
        System.exit(1);
      }

      else
      {
        return variables.get(identifier.value);
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

      return value.value;
    }

    return "";
  }
}

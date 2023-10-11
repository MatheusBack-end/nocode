import java.util.*;
import java.lang.reflect.*;

public class InterpreterUtils extends Consumer
{

  public Map<String, String> variables = new HashMap<String, String>();
  public Interpreter interpreter;

  public InterpreterUtils(Parser parser)
  {
    //this.interpreter = interpreter;
    super(parser);
  }

  public InterpreterUtils(List<Token> tokens)
  {
    //this.interpreter = interpreter;
    super(tokens);
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
          int result = Integer.valueOf(variables.get(identifier.value)) - Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(result);
        }
      }

      if(current_token.type.equals("sum"))
      {
        consume_token();

        if(current_token.type.equals("string"))
        {
          int result = Integer.valueOf(variables.get(identifier.value)) + Integer.valueOf(current_token.value);
          consume_token();

          return String.valueOf(result);
        }
      } 

      if(current_token.type.equals("operator"))
      {
        if(current_token.value.equals("menor"))
        {
          consume_token();
          boolean operation = Integer.valueOf(variables.get(identifier.value)) < Integer.valueOf(current_token.value);
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
    }

    return "";
  }
}

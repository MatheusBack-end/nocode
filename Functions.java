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
    super.package_names = interpreter.package_names;
    super.variables = interpreter.variables;
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
      //System.out.println(arg.getClass().getName());

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

   // System.out.println(current_token.type + " - " + current_token.value);

    if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("fim"))
    {
      consume_token(Token.Types.KEYWORD);

      return true;
    }

    if((current_token.type == Token.Types.KEYWORD) && (current_token.value.equals("retornar")))
    {
      consume_token();
      
      return_value = expression();
      return false;
    }

    if((current_token.type == Token.Types.KEYWORD) && (current_token.value.equals("se")))
    {
      consume_token();

      boolean codition = (boolean) expression();

      consume_token();

      //System.out.println(codition);

      if(!codition)
      {
        List<Token> if_block = new ArrayList<Token>();

        while(true)
        {
          if(current_token.type == Token.Types.KEYWORD)
          {
            if(current_token.value.equals("fim"))
              break;
          }

          if_block.add(current_token);
          consume_token();
        }

        //System.out.println(if_block);

        consume_token();

        return true;
      }

      //System.out.println("codition is true");
      return true;
    }

    if(current_token.type == Token.Types.IDENTIFIER)
    {
      Token identifier = current_token;
      consume_token();

      if(current_token.type == Token.Types.OPERATOR && (current_token.value.equals("+")))
      {
        super.variables.replace(identifier.value, ((int) super.variables.get(identifier.value) + 1));
        consume_token();

        return true;
      }

      if(current_token.type == Token.Types.OPARAM)
      {
        consume_token();

        List<Object> args = new ArrayList<Object>();

        while(current_token.type != Token.Types.CPARAM)
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
          Method method = Interpreter.class.getMethod(identifier.value, get_arg_types(args.toArray(new Object[0])));
          method.invoke(interpreter, (Object[]) args.toArray(new Object[0]));

          return true;
        }

        catch(Exception e)
        {
          System.out.println(e);
        }
      }

      if(current_token.type == Token.Types.EQUALS)
      {
        consume_token();
        
        Object value = expression();

        super.variables.put(identifier.value, value);
        return true;
      }

      if(current_token.type == Token.Types.DOT)
      {
        EvalDot dot_exp = new EvalDot(this, identifier);
        dot_exp.eval();
        
        return true;
      }
      
      //System.out.println(current_token.type + " " + current_token.value);
      System.exit(0);
    }

    return false;
  }
}

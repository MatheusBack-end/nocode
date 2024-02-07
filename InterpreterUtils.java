import java.util.*;
import java.lang.reflect.*;

@SuppressWarnings("deprecation")
public class InterpreterUtils extends Consumer
{

  public Map<String, Object> variables = new HashMap<String, Object>();
  public List<String> package_names = new ArrayList<String>();
  public Interpreter interpreter;
  public boolean in_math_expression = false;

  public InterpreterUtils(Tokenizer parser)
  {
    super(parser);
  }

  public InterpreterUtils(List<Token> tokens)
  {
    super(tokens);
  }

  public boolean expected(Token.Types token_type)
  {
    if(!(current_token.type == token_type))
      return false;

    consume_token(token_type);
    return true;
  }

  public Object in_package(String class_name, Class[] types, List<Object> args)
  {
    Object class_instance = null;

    for(String package_name: package_names)
    {
      try
      {
        class_instance = Class.forName(package_name + "." + class_name).getConstructor(types).newInstance((Object[]) args.toArray(new Object[0])); 
      }

      catch(ClassNotFoundException e)
      {
        //System.out.println(e);
      }

      catch(Exception e)
      {
        System.out.println(e.getCause());
      }

      if(class_instance != null)
      {
        return class_instance;
      }
    }

    return null;
  }
  
  public Object static_in_package(String class_name, String method_name, Class[] types, List<Object> args)
  {
    Object invoke_result = null;

    for(String package_name: package_names)
    {
      try
      {
        Class class_of_static = Class.forName(package_name + "." + class_name);
        Method method = class_of_static.getMethod(method_name, types);

        invoke_result = method.invoke(null, args.toArray(new Object[0])); 
      }

      catch(Exception e)
      {
        // ignore
      }

      if(invoke_result != null)
      {
        return invoke_result;
      }
    }

    return null;
  }

  public Object create_instance()
  {
    consume_token(Token.Types.KEYWORD);
    Token java_class_name = consume_token(Token.Types.IDENTIFIER);
    String result = "";
    result += java_class_name.value;

    if(current_token.type == Token.Types.DOT)
    {
      List<Token> trace = new ArrayList<Token>();

      while(!(current_token.type == Token.Types.OPARAM))
      {
        trace.add(current_token);
        consume_token();
      }
  
      for(Token t: trace)
      {
        result += t.value;
      }
    }

    consume_token(Token.Types.OPARAM);

    List<Object> args = new ArrayList<Object>();

    while(!(current_token.type == Token.Types.CPARAM))
    {
      args.add(expression());
    }

    consume_token(Token.Types.CPARAM);

    Object class_instance = null;
    Class[] types = new Class[args.size()];

    try
    {
      Object[] args_array = new Object[args.size()];

      for(int i = 0; i < types.length; i++)
      {
        Class<?> arg_type = args.get(i).getClass();

        if(arg_type.getName().equals("java.lang.Integer"))
          arg_type = int.class;

        if(arg_type.getName().equals("java.net.Inet4Address"))
          arg_type = java.net.InetAddress.class;

        if(arg_type.getName().equals("[B"))
          arg_type = byte[].class;

        types[i] = arg_type;
        args_array[i] = args.get(i);
      }

      // DEBUG 
      /*for(Class type: types)
      {
        //System.out.println(type.getName() + " > " + result);
      }

      for(Object type: args_array)
      {
        //System.out.println(type.toString() + " > " + result);
      }*/

      class_instance = Class.forName(result).getConstructor(types).newInstance((Object[]) args.toArray(new Object[0]));      
    }

    catch(Exception e)
    {
      //System.out.println(e + " " + e.getCause());
    }

    if(class_instance == null)
    {
      class_instance = in_package(result, types, args);
    }

    if(class_instance == null)
    {
      System.out.println("classe " + result + " não encontrada! ");
      System.exit(1);
    }

    return class_instance;
  }

  public List<String> get_static_method_name()
  {
    List<String> all_method = new ArrayList<String>();

    while(!(current_token.type == Token.Types.OPARAM))
    {
      all_method.add(current_token.value);
      consume_token();
    }

    return all_method;
  }

  public List<Object> get_args()
  {
    List<Object> args = new ArrayList<Object>();

    if(current_token.type == Token.Types.OPARAM)
    {
      boolean back_to_mt = false;

      if(in_math_expression)
      {
        in_math_expression = false;
        back_to_mt = true;
      }

      consume_token(Token.Types.OPARAM);
      
      while(!(current_token.type == Token.Types.CPARAM))
      {
        args.add(expression());
      }

      if(back_to_mt)
      {
        in_math_expression = true;
      }

      consume_token(Token.Types.CPARAM);
    }

    return args;
  }

  public Class[] get_arg_types(Object[] args)
  {
    Class[] types = new Class[args.length];

    for(int i = 0; i < args.length; i++)
    {
      Class class_type = args[i].getClass();

      if(class_type.getName().equals("java.lang.Interger"))
        class_type = int.class;

      types[i] = class_type;
    }

    return types;
  }

  public boolean is_boolean_expression()
  {
    if(current_token.type != Token.Types.KEYWORD)
      return false;

    if(current_token.value.equals("diferente")) return true;
    if(current_token.value.equals("igual")) return true;
    if(current_token.value.equals("maior")) return true;
    if(current_token.value.equals("menor")) return true;

    return false;
  }

  public boolean is_math_expression()
  {
    if(current_token.type != Token.Types.OPERATOR)
      return false;

    if(current_token.value.equals("+")) return true;
    if(current_token.value.equals("-")) return true;
    if(current_token.value.equals("/")) return true;
    if(current_token.value.equals("*")) return true;

    return false;
  }

  /*
   * simple boolean expression
   */
  public boolean boolean_expression(Object a)
  {
    if(current_token.value.equals("igual"))
    {
      consume_token(Token.Types.KEYWORD);
      Object b = expression();

      return a == b;
    }

    if(current_token.value.equals("diferente"))
    {
      consume_token(Token.Types.KEYWORD);
      Object b = expression();
  
      return a != b;
    }

    if(current_token.value.equals("menor"))
    {
      consume_token(Token.Types.KEYWORD);
      
      if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("igual"))
      {
        consume_token(Token.Types.KEYWORD);
        Object b = expression();

        return (int) a <= (int) b;
      }

      Object b = expression();

      return (int) a < (int) b;
    }

    if(current_token.value.equals("maior"))
    {
      consume_token(Token.Types.KEYWORD);

      if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("igual"))
      {
        consume_token(Token.Types.KEYWORD);
        Object b = expression();

        return (int) a >= (int) b;
      }

      Object b = expression();

      return (int) a > (int) b;
    }

    return false;
  }

  public Object expression()
  {
    //Scanner scanner = new Scanner(System.in);
    //scanner.nextLine();
    //current_token.print();
    //System.out.print(in_math_expression);

    if(current_token.type == Token.Types.KEYWORD && current_token.value.equals("criar"))
    {
      return create_instance();
    }

    if(current_token.type == Token.Types.KEYWORD && (current_token.value.equals("nulo")))
    {
      consume_token(Token.Types.KEYWORD);

      if(is_boolean_expression())
      {
        return boolean_expression(null);
      }

      return null;
    }

    if(current_token.type == Token.Types.NUMBER)
    {
      Token token_number = current_token;
      current_token.print();
      int number = Integer.parseInt(current_token.value);
      consume_token(Token.Types.NUMBER);

      if(is_boolean_expression() && !(in_math_expression))
      {
        return boolean_expression(number);
      }

      if(is_math_expression() && !(in_math_expression))
      {
        MathExpression exp = new MathExpression(this, token_number);
        return exp.eval();
      }

      return number;
    }

    if(current_token.type == Token.Types.IDENTIFIER)
    {
      Token identifier = current_token;
      consume_token();

      if(current_token.type == Token.Types.OPARAM)
      {
        boolean back_to_mt = false;

        if(in_math_expression)
        {
          back_to_mt = true;
          in_math_expression = false;
        }

        consume_token();

        List<Object> args = new ArrayList<Object>();
        while(!(current_token.type == Token.Types.CPARAM))
        {
          args.add(expression());
        }

        if(back_to_mt)
        {
          in_math_expression = true;
          back_to_mt = false;
        }

        consume_token();

        Object return_result = null;

        if(interpreter.functions.containsKey(identifier.value))
        {
          return_result = interpreter.invoke_function(identifier.value, args);
        }

        else
        {
          try
          {
            Method method = Interpreter.class.getMethod(identifier.value, String.class);
            return_result = method.invoke(interpreter, args.get(0));
          }

          catch(Exception e)
          {

          }
        }

        if(is_math_expression() && !(in_math_expression))
        {
          MathExpression exp = new MathExpression(this, new Token(String.valueOf(return_result), Token.Types.NUMBER, new Loc(0,0,0)));
          return_result = exp.eval();
        }

        if(return_result == null)
        {
          System.out.println("função: " + identifier.value + " não foi definida >:/");
          System.exit(1);
        }

        return return_result;
      }

      if(is_boolean_expression())
      {
        return boolean_expression(variables.get(identifier.value));
      }

      if(current_token.type == Token.Types.DOT)
      {
        EvalDot dot_exp = new EvalDot(this, identifier);
        return dot_exp.eval();
      }

      if(!variables.containsKey(identifier.value))
      {
        System.out.println("variavel: \"" + identifier.value + "\" não foi definida >:/");
        System.exit(1);
      }

      else
      {
        if(is_math_expression() && !(in_math_expression))
        {
          Token token_number = new Token(String.valueOf(variables.get(identifier.value)), Token.Types.NUMBER, new Loc(0,0,0));
          MathExpression exp = new MathExpression(this, token_number);
          
          return exp.eval();
        }

        return variables.get(identifier.value);
      }
    }

    if(current_token.type == Token.Types.STRING)
    {
      Token value = current_token;
      consume_token(Token.Types.STRING);

      if((current_token.type == Token.Types.IDENTIFIER || (current_token.type == Token.Types.CPARAM)))
      {
        return value.value;
      }

      return value.value;
    }

    if(current_token.type == Token.Types.LITERAL_BOOLEAN)
    {
      Token literal_boolean = consume_token(Token.Types.LITERAL_BOOLEAN);

      return literal_boolean.value.equals("true");
    }

    return "";
  }
}

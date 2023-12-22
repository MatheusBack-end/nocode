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

    List<Object> args = new ArrayList<Object>();

    while(!current_token.type.equals("cparam"))
    {
      args.add(expression());
    }

    consume_token("cparam");

    Object class_instance = null;

    try
    {
      Class[] types = new Class[args.size()];
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
      System.out.println(e + " " + e.getCause());
    }

    return class_instance;
  }

  public List<String> get_static_method_name()
  {
    List<String> all_method = new ArrayList<String>();

    while(!current_token.type.equals("oparam"))
    {
      all_method.add(current_token.value);
      consume_token();
    }

    return all_method;
  }

  public List<Object> get_args()
  {
    List<Object> args = new ArrayList<Object>();

    if(current_token.type.equals("oparam"))
    {
      consume_token("oparam");
      
      while(!current_token.type.equals("cparam"))
      {
        args.add(expression());
      }

      consume_token("cparam");
    }

    return args;
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

        List<Object> args = new ArrayList<Object>();
        while(!current_token.type.equals("cparam"))
        {
          args.add(expression());
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

      if(current_token.type == "dot")
      {
        Object invoke_value = null;
        Class source_class = null;

        if(!variables.containsKey(identifier.value))
        {
          // static methods

          List<String> static_method = get_static_method_name();
          String origin_class = identifier.value; 
          String method_name = static_method.get(static_method.size() - 1);

          for(int i = 0; i < static_method.size() - 2; i++)
          {
            origin_class += static_method.get(i);
          }

          List<Object> args = get_args();
          Class[] arg_types = new Class[args.size()];

          for(int i = 0; i < arg_types.length; i++)
          {
            Class arg_type = args.get(i).getClass();

            if(arg_type.getName().equals("java.lang.Integer"))
              arg_type = args.get(i).getClass();

            arg_types[i] = arg_type;
          }

          try
          {
            Class class_of_static = Class.forName(origin_class);
            Method method = class_of_static.getMethod(method_name, arg_types);

            return method.invoke(null, args.toArray(new Object[0]));
          }

          catch(Exception e)
          {
              System.out.println(e + " " + e.getCause());
          }
        }

        while(true)
        {
          consume_token("dot");
          String method_name = current_token.value;
          consume_token("identifier");

          List<Object> args = new ArrayList<Object>();
          if(current_token.type.equals("oparam"))
          {
            consume_token("oparam");
    
            while(current_token.type != "cparam")
            {
              args.add(expression());
            }
    
            consume_token("cparam");
          }

          Class[] arg_types = new Class[args.size()];
              
          for(int i = 0; i < args.size(); i++)
          {
            if(args.get(i) == null)
              continue;
                
            Class object_class = args.get(i).getClass();

            if(object_class.getName().equals("java.lang.Integer"))
              object_class = int.class;

            arg_types[i] = object_class;
          }

          if(invoke_value == null)
          {
            if(variables.containsKey(identifier.value))
            {
              try
              {
                Method method = variables.get(identifier.value).getClass().getMethod(method_name, arg_types);
                invoke_value = method.invoke(variables.get(identifier.value), (Object[]) args.toArray(new Object[0]));
              }

              catch(Exception e)
              {
                System.out.println(e + " " + e.getCause());
              }

              if(current_token.type.equals("dot"))
                continue;

              return invoke_value;
            }
          }

          // seconds or others calls
          //
   
          try
          {
            Method method = invoke_value.getClass().getMethod(method_name, arg_types);
            invoke_value = method.invoke(invoke_value, (Object[]) args.toArray(new Object[0]));
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

        if(invoke_value != null)
          return invoke_value;
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

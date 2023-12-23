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

  @SuppressWarnings("deprecation")
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

      if(current_token.type == "dot")
      {
        Object invoke_value = null;

        Class source_class = null;

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

              return true;
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
          return true;

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
            List<Object> args = new ArrayList<Object>();

            while(current_token.type != "cparam")
            {
              args.add(expression());
            }

            consume_token("cparam");

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
            
            try
            {
              Method  method = source_class.getMethod(name.value, arg_types);
              method.invoke(this, (Object[]) args.toArray(new Object[0]));
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

        List<Object> args = new ArrayList<Object>();

        while(current_token.type != "cparam")
        {
          args.add(expression());
        }

        consume_token("cparam");

        if(functions.containsKey(identifier.value))
        {
          invoke_function(identifier.value, args);
          return true;
        }

        try
        {
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

import java.util.*;
import java.lang.reflect.*;

public class EvalDot
{
  private InterpreterUtils interpreter;
  private Token initial_identifier;

  public EvalDot(InterpreterUtils interpreter, Token initial_identifier)
  {
    this.interpreter = interpreter;
    this.initial_identifier = initial_identifier;
  }

  public Object eval()
  {
    if(interpreter.current_token.type.equals("dot"))
    {
      Object invoke_value = null;
      Class source_class = null;

      if(!interpreter.variables.containsKey(initial_identifier.value))
      {
        List<String> static_method = interpreter.get_static_method_name();
        String origin_class = initial_identifier.value;
        String method_name = static_method.get(static_method.size() - 1);

        for(int i = 0; i < static_method.size() - 2; i++)
        {
          origin_class += static_method.get(i);
        }

        List<Object> args = interpreter.get_args();
        Class[] arg_types = new Class[args.size()];

        for(int i = 0; i < arg_types.length; i++)
        {
          Class arg_type = args.get(i).getClass();

          if(arg_type.getName().equals("java.lang.Integer"))
            arg_type = args.get(i).getClass();

          arg_types[i] = arg_type;
        }

        Object invoke_result = null;

        try
        {
          Class class_of_static = Class.forName(origin_class);
          Method method = class_of_static.getMethod(method_name, arg_types);

          invoke_result = method.invoke(null, args.toArray(new Object[0]));
        }

        catch(Exception e)
        {
          // ignore
        }

        if(invoke_result == null)
        {
          invoke_result = interpreter.static_in_package(origin_class, method_name, arg_types, args);
        }

        if(invoke_result == null)
        {
          System.out.println("função estatica " + origin_class + "->" + method_name + " não encontrada!");
          System.exit(0);
        }

        return invoke_result;
      }

      while(true)
      {
        interpreter.consume_token("dot");
        String method_name = interpreter.consume_token("identifier").value;

        List<Object> args = new ArrayList<Object>();
        
        if(interpreter.current_token.type.equals("oparam"))
        {
          interpreter.consume_token("oparam");
    
          while(interpreter.current_token.type != "cparam")
          {
            args.add(interpreter.expression());
          }
    
          interpreter.consume_token("cparam");
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
          if(interpreter.variables.containsKey(initial_identifier.value))
          {
            try
            {
              Method method = interpreter.variables.get(initial_identifier.value).getClass().getMethod(method_name, arg_types);
              invoke_value = method.invoke(interpreter.variables.get(initial_identifier.value), (Object[]) args.toArray(new Object[0]));
            }

            catch(Exception e)
            {
              System.out.println(e + " " + e.getCause());
            }

            if(interpreter.current_token.type.equals("dot"))
              continue;

            return invoke_value;
          }
        }

        // seconds or others calls   
          
        try
        {
          Method method = invoke_value.getClass().getMethod(method_name, arg_types);
          invoke_value = method.invoke(invoke_value, (Object[]) args.toArray(new Object[0]));
        }

        catch(Exception e)
        {
          System.out.println(e);
        }

        if(!interpreter.current_token.type.equals("dot"))
        {
          break;
        }
      }

      if(invoke_value != null)
        return invoke_value;
    }

    return null;
  }
}

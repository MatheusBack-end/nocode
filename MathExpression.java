import java.util.*;
import java.lang.reflect.*;

public class MathExpression
{
  private InterpreterUtils interpreter;
  private Token initial_identifier;

  public MathExpression(InterpreterUtils interpreter, Token initial_identifier)
  {
    this.interpreter = interpreter;
    this.initial_identifier = initial_identifier;
  }

  public int get_result(String type, int value_a, int value_b)
  {
    switch(type)
    {
      case "+":
        return value_a + value_b;

      case "-":
        return value_a - value_b;

      case "/":
        return value_a / value_b;

      case "*":
        return value_a * value_b;
    }

    return 0;
  }

  public Object eval()
  {
    interpreter.in_math_expression = true;

    if(interpreter.current_token.type == Token.Types.OPERATOR)
    {
      String type = interpreter.current_token.value;

      interpreter.consume_token(Token.Types.OPERATOR);

      int value_a = Integer.parseInt(initial_identifier.value);
      int value_b = (int) interpreter.expression();
        
      int result = get_result(type, value_a, value_b);

      while(interpreter.is_math_expression())
      {
        type = interpreter.current_token.value;
        interpreter.consume_token(Token.Types.OPERATOR);

        value_a = result;
        value_b = (int) interpreter.expression();

        result = get_result(type, value_a, value_b);
      }

      interpreter.in_math_expression = false;
      return result;
    }

    return null;
  }
}

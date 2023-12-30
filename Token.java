public class Token
{
  public String value;
  public Types type;

  public Token(String value, Types type)
  {
    this.value = value;
    this.type = type;

    //System.out.println(value + " " + type);
  }

  public enum Types
  {
    OPARAM,
    CPARAM,
    KEYWORD,
    CLOSE_BLOCK,
    BLOCK,
    IDENTIFIER,
    NUMBER,
    OPERATOR,
    EOF,
    STRING,
    DOT,
    EQUALS,
    SUB,
    SUM,
    LITERAL_BOOLEAN,
    NULL
  };
}

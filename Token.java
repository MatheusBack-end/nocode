public class Token
{
  public String value;
  public Types type;
  public Loc loc;

  public Token(String value, Types type, Loc loc)
  {
    this.value = value;
    this.type = type;
    this.loc = loc;

    var_dump();
  }

  public void print()
  {
    System.out.println(value + " " + type);
  }

  public void var_dump()
  {
    System.out.println(value + " " + type + " " + loc.toString());
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

class Loc
{
  public int start;
  public int end;
  public int line;

  public Loc(int start, int end, int line)
  {
    this.start = start;
    this.end = end;
    this.line = line;
  }

  @Override
  public String toString()
  {
    return "[ " + start + " " + end + " " + line + " ]";
  }
}

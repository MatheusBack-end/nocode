import java.io.*;
import java.util.*;

class Main
{
  public static void main(String[] args)
  {
    if(args.length < 1)
    {
      System.out.println("\u001B[31merro:\u001B[0m éra esperado argumento <ARQUIVO>");
      System.exit(1);
    }

    String file_data = read_file(args[0]);
    new Interpreter(new Parser(file_data));
  }

  public static String read_file(String path)
  {
    String data = "";
    
    try
    {
      File file = new File(path);
      Scanner reader = new Scanner(file);
      while(reader.hasNextLine())
      {
        data += reader.nextLine() + "\n";
      }

      data = data.substring(0, data.length() - 1);
    }

    catch(Exception e)
    {
      System.out.println(e);
    }

    return data;
  }
}

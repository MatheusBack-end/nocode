import java.io.*;
import java.util.*;

class Main
{
  public static void main(String[] args)
  {
    String file_data = read_file(args[1]);
    Parser parser = new Parser(file_data);
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
        data += reader.nextLine();
      }
    }

    catch(Exception e)
    {
      System.out.println(e);
    }

    return data;
  }
}

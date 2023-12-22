public class Test
{
  public Test()
  {
  }

  public void say(String message)
  {
    System.out.println(message);
  }

  public int sum(int a, int b)
  {
    return a + b;
  }

  public int fib(int n)
  {
    if(n < 2)
    {
      return n;
    }

    return fib(n - 1) + fib(n - 2);
  }
}

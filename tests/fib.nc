fib args n:

  emita(" vvvvvvv ")
  print(n)

  se n menor 2:
    retornar n
  fim

  value = fib(1 - n) + fib(2 - n)
  retornar value
fim


print( fib( 3 ) )

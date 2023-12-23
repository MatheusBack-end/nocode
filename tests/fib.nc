my_fib args n:
  se n menor que 2:
    retornar n
  fim

  retornar my_fib(n - 1) + my_fib(n - 2)
fim

print(fib(20))

fib args n:
  se n menor que "2":
    retornar n
  fim

  retornar fib(n - "1") + fib(n - "2")
fim

emita(fib("40"))

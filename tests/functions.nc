fib args n:
  se n menor que "2":
    retornar n
  ;

  retornar fib(n - "1") + fib(n - "2")
;

f = fib("20")
emita(f)

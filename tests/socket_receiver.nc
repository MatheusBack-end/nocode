pacote "java.net"
pacote "java.lang"

soquete = criar DatagramSocket(19132)

repeticao:
  memoria_temporaria = "                  "

  recebido = criar DatagramPacket(memoria_temporaria.getBytes() memoria_temporaria.length()) 
  soquete.receive(recebido)

  se recebido diferente de nulo:
    dado = criar String(recebido.getData() 0 recebido.getLength())
    emita(dado)
  fim
  
  repeticao()
fim

repeticao()
soquete.close()

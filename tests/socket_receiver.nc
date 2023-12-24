pacote "java.net"
pacote "java.lang"

soquete = criar DatagramSocket(19132)
memoria_temporaria = criar String("                  ")
recebido = criar DatagramPacket(memoria_temporaria.getBytes() memoria_temporaria.length())
soquete.receive(recebido)

se recebido diferente de nulo:
  dado = criar String(recebido.getData() 0 recebido.getLength())
  emita(dado)   
  soquete.close()
fim

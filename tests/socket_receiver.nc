pacote "java.net"
pacote "java.lang"

soquete = criar DatagramSocket(19132)
buffer = criar String("                  ")
pacote_ = criar DatagramPacket(buffer.getBytes() buffer.length())
soquete.receive(pacote_)
erro = nulo

se pacote_ diferente nulo:
  dado = criar String(pacote_.getData() 0 pacote_.getLength())
  emita(dado)   
  soquete.close()
fim

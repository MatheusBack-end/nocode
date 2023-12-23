pacote "java.net"

socket = criar DatagramSocket()
ip = InetAddress.getByName("127.0.0.1")
message = "hello receiver =]"

packet = criar DatagramPacket(message.getBytes() message.length() ip 19132)
socket.send(packet)
socket.close()

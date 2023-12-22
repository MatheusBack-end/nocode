socket = criar java.net.DatagramSocket()
ip = java.net.InetAddress.getByName("127.0.0.1")
message = "test"

packet = criar java.net.DatagramPacket(message.getBytes() 4 ip 19132)
socket.send(packet)
socket.close()

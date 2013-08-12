package netis
import java.net.URL
import sun.security.krb5.internal.TCPClient
import sun.security.krb5.internal.TCPClient
import javax.net.SocketFactory
import java.nio.channels.SocketChannel
import java.net.SocketOptions
import java.net.StandardSocketOptions
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.net.SocketAddress
import java.nio.charset.CharsetDecoder
import java.nio.charset.Charset

object NetisClientApp extends App {
  val sock = SocketChannel.open(new InetSocketAddress("localhost", 9090))
  if (sock.isOpen()) {
    sock.configureBlocking(true)
    val decoder = Charset.defaultCharset().newDecoder()
    sock.write(ByteBuffer.wrap("java/param1=1".getBytes()))
    val buf = ByteBuffer.allocate(1024)
    while (sock.read(buf) != -1) {
      buf.flip()
      println(" " + decoder.decode(buf).toString())
      buf.clear()
    }
    sock.close()
  } 
}
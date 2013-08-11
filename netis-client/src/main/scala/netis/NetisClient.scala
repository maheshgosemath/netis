package netis
import java.net.URL
import uk.co.bigbeeconsultants.http.header.Headers
import uk.co.bigbeeconsultants.http.header.Header
import uk.co.bigbeeconsultants.http.HttpClient
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

object NetisClient {
  def apply() = {
    val headers = Headers(new Header("apikey", ""))
    val httpClient = new HttpClient
    new NetisClient(httpClient, headers)
  }

  val prepUrl = (req: String) => new URL("http://localhost:9090/")
}

class NetisClient(client: HttpClient, headers: Headers) {
  import NetisClient._

  def getResponse(url: String) = {
    client.get(prepUrl(url), headers).body.asString
  }

  def postRequest(url: String) = {
  }

}

object NetisClientApp extends App {
  val k = NetisClient()
  val sock = SocketChannel.open(new InetSocketAddress("localhost", 9090))
  if (sock.isOpen()) {
    sock.configureBlocking(true)
    val decoder = Charset.defaultCharset().newDecoder()
    sock.write(ByteBuffer.wrap("java".getBytes()))
    val buf = ByteBuffer.allocate(1024)
    while(sock.read(buf) != -1) {
      buf.flip()
      println(" " + decoder.decode(buf).toString())
      buf.clear()
    }
  }
  sock.close()
}
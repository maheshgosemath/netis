package netis

import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.buffer.UnpooledUnsafeDirectByteBuf
import io.netty.buffer.EmptyByteBuf
import io.netty.handler.codec.marshalling.ChannelBufferByteOutput
import java.net.InetSocketAddress
import scala.collection.mutable.Map

object NetisServerCommons {

  val resolveQueryString = (msg: Object) => {
    val byteBuf = msg.asInstanceOf[UnpooledUnsafeDirectByteBuf]
    val reqStr = new StringBuilder
    while (byteBuf.isReadable()) {
      reqStr.append(byteBuf.readByte().asInstanceOf[Char])
    }
    byteBuf.release()
    reqStr.toString()
  }

  val obtainRequest = (url: String) => url.split("/")(0)

  val obtainRequestParam = (url: String) => {
    val buffer = Map[String, String]()
    url.split("/")(1).split("&").foreach(str => {
      buffer.+=((str.split("=")(0), str.split("=")(1)))
    })
    buffer
  }
}

object HandlerType {
  type handlerDel = (String, Map[String, String]) => String
}

import netis.HandlerType._

class NetisRequestHandler(handler: handlerDel)
  extends ChannelInboundHandlerAdapter {

  import netis.NetisServerCommons.{ resolveQueryString, obtainRequest, obtainRequestParam }

  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    val reqUrl = resolveQueryString(msg)
    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(
      handler(obtainRequest(reqUrl), obtainRequestParam(reqUrl)).getBytes()))
  }

}

object NetisServer {
  def apply(port: Int, handlr: handlerDel) =
    new NetisServer(host = "localhost", port, handlr)
}

class NetisServer(host: String, port: Int, handlr: handlerDel) {

  val boss = new NioEventLoopGroup
  val worker = new NioEventLoopGroup

  val serverBootStrap: ServerBootstrap = new ServerBootstrap()
    .group(boss, worker)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(new ChannelInitializer[SocketChannel]() {
      override def initChannel(channel: SocketChannel) {
        channel.pipeline().addLast(new NetisRequestHandler(handlr))
      }
    })

  def run = {
    try {
      println("Netis running on " + host + " @port " + port + " ... ")
      val bootstrap = serverBootStrap.bind(
        new InetSocketAddress(host, port)).sync().channel()
      bootstrap.closeFuture().sync()
    } finally {
      stop
    }
  }

  def stop {
    boss.shutdownGracefully()
    worker.shutdownGracefully()
  }
}
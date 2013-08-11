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
}

class NetisRequestHandler(handler: String => String)
  extends ChannelInboundHandlerAdapter {
  import netis.NetisServerCommons._
  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(
      handler(resolveQueryString(msg)).getBytes()))
  }
}

object NetisServer {
  def apply(port: Int, handlr: String => String) =
    new NetisServer(host = "localhost", port, handlr)
}

class NetisServer(host: String, port: Int, handlr: String => String) {

  val boss = new NioEventLoopGroup
  val worker = new NioEventLoopGroup

  val serverBootStrap: ServerBootstrap = new ServerBootstrap()
    .group(boss, worker)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(new ChannelInitializer[SocketChannel]() {
      override def initChannel(chnnel: SocketChannel) {
        chnnel.pipeline().addLast(new NetisRequestHandler(handlr))
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
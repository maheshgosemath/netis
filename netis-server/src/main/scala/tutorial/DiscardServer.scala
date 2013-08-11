package tutorial

import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.buffer.UnpooledUnsafeDirectByteBuf

class NetisDiscardServerHandler extends ChannelInboundHandlerAdapter {
  println("Handler init")
  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    val byteBuf = msg.asInstanceOf[UnpooledUnsafeDirectByteBuf]
    while(byteBuf.isReadable()) {
      print(byteBuf.readByte().asInstanceOf[Char])
    }
    byteBuf.release()
  }
}

class NetisDiscardServer {
  val host = "localhost"
  val port = 9090
  val boss = new NioEventLoopGroup
  val worker = new NioEventLoopGroup
  val serverBootStrap: ServerBootstrap = new ServerBootstrap()
    .group(boss, worker)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(new ChannelInitializer[SocketChannel]() {
      override def initChannel(chnnel: SocketChannel) {
        chnnel.pipeline().addLast(new NetisDiscardServerHandler())
      }
    })

  def run = {
    try {
      val bootstrav = serverBootStrap.bind(port).sync().channel()
      bootstrav.closeFuture().sync()
    } finally {
      stop
    }
  }

  def stop {
    boss.shutdownGracefully()
    worker.shutdownGracefully()
  }
}

object ServerApp extends App {
  val disc = new NetisDiscardServer()
  disc.run
}
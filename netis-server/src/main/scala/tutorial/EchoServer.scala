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
import io.netty.buffer.EmptyByteBuf
import io.netty.handler.codec.marshalling.ChannelBufferByteOutput

class NetisEchoServerHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("java".getBytes()))
  }
}

class NetisEchoServer {
  val host = "localhost"
  val port = 9090
  val boss = new NioEventLoopGroup
  val worker = new NioEventLoopGroup
  val serverBootStrap: ServerBootstrap = new ServerBootstrap()
    .group(boss, worker)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(new ChannelInitializer[SocketChannel]() {
      override def initChannel(chnnel: SocketChannel) {
        chnnel.pipeline().addLast(new NetisEchoServerHandler())
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

object EchoServerApp extends App {
  val disc = new NetisEchoServer()
  disc.run
}
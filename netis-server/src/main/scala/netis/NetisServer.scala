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

class NetisEchoServerHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    NetisServerCommons.resolveQueryString(msg)
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
      val bootstrap = serverBootStrap.bind(port).sync().channel()
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
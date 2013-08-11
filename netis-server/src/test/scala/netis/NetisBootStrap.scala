package netis

object NetisServerApp extends App {
  val disc = new NetisEchoServer()
  disc.run
}
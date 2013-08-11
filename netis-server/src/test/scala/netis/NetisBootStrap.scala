package netis

object NetisServerApp extends App {
  val disc = new NetisServer("localhost", 9090)
  disc.run
}
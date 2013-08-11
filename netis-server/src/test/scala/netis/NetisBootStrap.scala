package netis

object NetisServerApp extends App {
  NetisServer(9090, (url: String) => {
    url match {
      case "java" => "returning java"
      case "scala" => "returning scala"
    }
  }).run
}
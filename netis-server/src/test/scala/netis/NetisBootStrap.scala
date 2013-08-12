package netis

import scala.collection.mutable.Map

object NetisServerApp extends App {

  NetisServer(9090, (url, params) => {
    url match {
      case "java" => {
        "returning java"
      }
      case "scala" => "returning scala"
    }
  }).run

}


package netis

import scala.collection.mutable.Map

object NetisServerApp extends App {

  NetisServer(9090, (url, params) => {
    url match {
      case "java" => {
        params("param1") + " > " + 
        "returning java"
      }
      case "scala" => "returning scala"
    }
  }).run

}


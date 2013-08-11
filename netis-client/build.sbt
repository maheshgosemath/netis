name := "netis-client"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
    "uk.co.bigbeeconsultants" %% "bee-client" % "0.21.+",
    "org.slf4j" % "slf4j-api" % "1.7.+"
)

resolvers += "Big Bee Consultants" at "http://repo.bigbeeconsultants.co.uk/repo"
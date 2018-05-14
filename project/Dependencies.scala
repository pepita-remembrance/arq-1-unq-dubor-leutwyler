import play.sbt._
import sbt._

object Dependencies {

  val appDependencies = Seq(
    PlayImport.guice,
    "org.postgresql" % "postgresql" % "42.2.2",
    "com.github.nscala-time" %% "nscala-time" % "2.16.0",
    "com.typesafe.play" %% "play-json" % "2.6.2",
    "com.typesafe.play" %% "play-json-joda" % "2.6.2",
    "org.squeryl" %% "squeryl" % "0.9.7",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "net.codingwell" %% "scala-guice" % "4.1.1",
    "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"

  )

  lazy val testDependecies = Seq(
    "com.h2database" % "h2" % "1.4.196",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0"
  ).map(_ % Test)

}

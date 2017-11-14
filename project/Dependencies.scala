import sbt._
import play.sbt._

object Dependencies {

  val appDependencies = Seq(
    "com.h2database" % "h2" % "1.4.196",                    //TODO: Replace with real database driver
    PlayImport.guice,
    "com.github.nscala-time" %% "nscala-time" % "2.16.0",
    "com.typesafe.play" %% "play-json" % "2.6.0",
    "com.typesafe.play" %% "play-json-joda" % "2.6.0",
    "org.squeryl" %% "squeryl" % "0.9.7",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val testDependecies = Seq(
    "com.h2database" % "h2" % "1.4.196",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0"
  ).map(_ % Test)

}

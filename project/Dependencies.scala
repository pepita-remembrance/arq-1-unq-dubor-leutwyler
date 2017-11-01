import sbt._
import play.sbt._

object Dependencies {

  val appDependencies = Seq(
    PlayImport.guice,
    "com.typesafe.play" %% "play-json" % "2.6.0",
    "org.squeryl" %% "squeryl" % "0.9.7"
  )

  lazy val testDependecies = Seq(
    "com.h2database" % "h2" % "1.4.196",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0"
  ).map(_ % Test)

}

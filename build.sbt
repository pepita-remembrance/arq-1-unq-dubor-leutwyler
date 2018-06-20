name := """arq-1-unq-scala-api-dubor-leutwyler"""
organization := "pepita-remembrance"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
enablePlugins(NewRelic)

scalaVersion := "2.12.3"

libraryDependencies ++= Dependencies.appDependencies
libraryDependencies ++= Dependencies.testDependecies

newrelicVersion := "4.1.0"

newrelicLicenseKey := Some("1c58ac08ed5c97e3160f2c32d83071b2181a7f04")

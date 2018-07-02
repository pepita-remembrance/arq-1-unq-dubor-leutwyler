name := """arq-1-unq-scala-api-dubor-leutwyler"""
organization := "pepita-remembrance"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
enablePlugins(NewRelic)

scalaVersion := "2.12.3"

libraryDependencies ++= Dependencies.appDependencies
libraryDependencies ++= Dependencies.testDependecies

newrelicVersion := "4.1.0"

newrelicLicenseKey := Some("933148e3552cd65eff4f4cfc53c823fa89763e65")

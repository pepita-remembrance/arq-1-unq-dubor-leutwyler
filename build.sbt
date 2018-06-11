name := """arq-1-unq-scala-api-dubor-leutwyler"""
organization := "pepita-remembrance"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Dependencies.appDependencies
libraryDependencies ++= Dependencies.testDependecies